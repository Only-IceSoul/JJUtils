package com.jjlf.jjkit_utils.extension

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat


fun Activity.isCameraPermissionGranted(): Boolean{
    return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}

fun Activity.isReadWritePermissionGranted():Boolean{
    return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            &&  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermissionCamera(requestCode:Int){
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), requestCode)
}

fun Activity.requestPermissionReadWrite(context: Activity, requestCode:Int){
    ActivityCompat.requestPermissions(context, arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
}

fun Activity.isPermissionGranted(permission: String):Boolean{
    return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Activity.startActivitySettingsLocation(){
    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}

fun Activity.startActivitySettingsApp(){
    val packageUri = Uri.fromParts("package", packageName, null)
    val applicationDetailsSettingsIntent = Intent()
    applicationDetailsSettingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    applicationDetailsSettingsIntent.data = packageUri
    applicationDetailsSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(applicationDetailsSettingsIntent)
}

fun Activity.requestOverlayPermission(requestCode: Int) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${packageName}")
        )
        startActivityForResult(intent, requestCode)
    }
}

fun Activity.isOverlayPermissionGranted():Boolean {
    var result = true
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
        result = Settings.canDrawOverlays(this)
    }
    return result
}

fun Activity.shouldShowPermissionRationale(permission:String): Boolean{
    var v = true
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) v = this.shouldShowRequestPermissionRationale(permission)
    return v
}
