package com.jjlf.jjkit_utils.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.Exception


var Activity.MATCH_PARENT: Int
    get() = -1
    set(value) { }

var Activity.WRAP_CONTENT: Int
    get() = -2
    set(value) { }

fun Activity.addViewToWindow(view: View, params: WindowManager.LayoutParams){
    val wm = this.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
    wm.addView(view, params)
}

fun Activity.removeViewFromWindow(view: View){
    val wm = this.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
    wm.removeView(view)
}

//@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("DiscouragedPrivateApi")
fun Activity.transparent(){
    try {
        val getActivityOptions = Activity::class.java.getDeclaredMethod("getActivityOptions")
        getActivityOptions.isAccessible = true
        val options = getActivityOptions.invoke(this)

        val classes = Activity::class.java.declaredClasses
        var translucentConversionListenerClazz: Class<*>? = null
        for (clazz in classes) {
            if (clazz.simpleName.contains("TranslucentConversionListener")) {
                translucentConversionListenerClazz = clazz
            }
        }
        val convertToTranslucent = Activity::class.java.getDeclaredMethod(
            "convertToTranslucent",
            translucentConversionListenerClazz, ActivityOptions::class.java
        )
        convertToTranslucent.isAccessible = true
        convertToTranslucent.invoke(this, null, options)

        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    catch (e: Exception){
        Log.e("JJKit","ERROR : $e")
    }
}

fun Activity.removeKeyboard(){
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val cf = this.currentFocus ?:  View(this)
    imm.hideSoftInputFromWindow(cf.windowToken, 0)
}

fun Activity.removeKeyboard(focus:View){
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(focus.windowToken, 0)
}

fun Activity.showKeyboard(focus:View){
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(focus, InputMethodManager.SHOW_IMPLICIT)
}


fun Activity.statusBarBlack() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.statusBarWhite() {
    window.decorView.systemUiVisibility = 0
}

fun Activity.statusBarBackgroundColor(bgColor: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = bgColor
    }
}

fun Activity.statusBarBackgroundColorAboveM(bgColor: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = bgColor
    }
}


fun Activity.statusBarBackgroundColor(belowM: Int,aboveM: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = aboveM
    }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        window.statusBarColor = belowM
    }
}


@Suppress("DEPRECATION")
fun Activity.getOverlayTypeCompact() : Int {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
}
@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Activity.haveNetworkConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }

}


fun Activity.SoftInputModeResize(){
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}



fun Activity.addViewToWindowFullScreen(v: View){
    val wm = getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
    val params = WindowManager.LayoutParams()
    params.type =  WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
    params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
    params.format = PixelFormat.TRANSLUCENT
    params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
    wm.addView(v, params)
}

fun Activity.toastShort(msg:String){
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
}

fun Activity.toastLong(msg:String){
    Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
}

fun Activity.toastShort(res:Int){
    Toast.makeText(this,res, Toast.LENGTH_SHORT).show()
}

fun Activity.toastLong(res:Int){
    Toast.makeText(this,res, Toast.LENGTH_LONG).show()
}

fun Activity.alert(msg:String){
    MaterialAlertDialogBuilder(this)
        .setMessage(msg)
        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.create()
        .show()
}

fun Activity.alert(res:Int){
    MaterialAlertDialogBuilder(this)
        .setMessage(resources.getString(res))
        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.create()
        .show()
}

fun Activity.alert(msg:String,onClick:()->Unit){
    MaterialAlertDialogBuilder(this)
        .setMessage(msg)
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            onClick.invoke()
        }.create()
        .show()
}

fun Activity.alert(res:Int,onClick: () -> Unit){
    MaterialAlertDialogBuilder(this)
        .setMessage(resources.getString(res))
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            onClick.invoke()
        }.create()
        .show()
}


fun Activity.getColorCompat(resId:Int): Int {
    return ContextCompat.getColor(this, resId)
}
fun Activity.getDrawableCompat(resId:Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}
fun Activity.getFontCompat(resId:Int): Typeface? {
    return ResourcesCompat.getFont(this,resId)
}



fun Activity.getRawView(id:Int): View {
    return findViewById<View>(id)
}

fun  Activity.getTextView(id:Int): TextView {
    return findViewById<TextView>(id)
}

fun  Activity.getImageView(id:Int): ImageView {
    return findViewById<ImageView>(id)
}

fun Activity.getButton(id:Int): Button {
    return findViewById<Button>(id)
}

fun <T: View> Activity.getView(id:Int): T {
    return findViewById(id)
}

fun <T: View> Activity.getView(parent:Int,child:Int): T {
    return findViewById<ViewGroup>(parent).findViewById(child) as T
}

fun <T: View> Activity.getView(one:Int,two:Int,three:Int): T {
    return findViewById<ViewGroup>(one).findViewById<ViewGroup>(two).findViewById(three) as T
}
