package com.jjlf.jjkit_utils.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception


fun Activity.addViewToWindow(view: View, params: WindowManager.LayoutParams){
    val wm = this.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
    wm.addView(view, params)
}

fun Activity.removeViewFromWindow(view: View){
    val wm = this.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
    wm.removeView(view)
}


@SuppressLint("DiscouragedPrivateApi")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
        Log.e("JJKit","ERROR : ${e.stackTrace}")
    }
}

fun Activity.removeKeyboard(){
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val cf = this.currentFocus ?:  View(this)
    imm.hideSoftInputFromWindow(cf.windowToken, 0)
}

fun Activity.showKeyboard(focus:View){
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(focus, InputMethodManager.SHOW_IMPLICIT)
}


fun Activity.statusBarBlack(context: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.statusBarWhite(context: Activity) {
    context.window.decorView.systemUiVisibility = 0
}

fun Activity.statusBarBackgroundColor(context: Activity, bgColor: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        context.window.statusBarColor = bgColor
    }
}
fun Activity.statusBarBackgroundColorRes(context: Activity, colorRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val bgColor = ContextCompat.getColor(context,colorRes)
        context.window.statusBarColor = bgColor
    }
}

fun Activity.statusBarBackgroundColor(context: Activity, belowM: Int,aboveM: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.window.statusBarColor = aboveM
    }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        context.window.statusBarColor = belowM
    }
}


fun Activity.statusBarBackgroundColorRes(context: Activity, resBelowM: Int, resAboveM: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val aboveM = ContextCompat.getColor(context,resAboveM)
        context.window.statusBarColor = aboveM
    }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        val belowM = ContextCompat.getColor(context,resBelowM)
        context.window.statusBarColor = belowM
    }
}

fun Activity.getOverlayTypeCompact() : Int {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
}

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


