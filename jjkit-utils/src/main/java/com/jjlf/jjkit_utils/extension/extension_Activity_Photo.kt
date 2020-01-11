package com.jjlf.jjkit_utils.extension

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.File
import java.util.ArrayList

fun Activity.getIntentImageChooser(title: CharSequence? = null, fileProvider:Boolean = false,
                                   authorityProvider: String? = null,
                                   includeDocuments: Boolean = true,
                                   includeCamera: Boolean = true, sender: IntentSender? = null): Intent {
    val allIntents = ArrayList<Intent>()

    // collect all camera intents if Camera permission is available
    if (includeCamera) {
        allIntents.addAll(getCameraIntents(fileProvider,authorityProvider))
    }

    var galleryIntents = getPhotoIntents(Intent.ACTION_GET_CONTENT, includeDocuments)
    if (galleryIntents.isEmpty()) {
        // if no intents found for get-content try pick intent action (Huawei P9).
        galleryIntents = getPhotoIntents(Intent.ACTION_PICK, includeDocuments)
    }
    allIntents.addAll(galleryIntents)

    val target: Intent
    if (allIntents.isEmpty()) {
        target = Intent()
    } else {
        target = allIntents[allIntents.size - 1]
        allIntents.removeAt(allIntents.size - 1)
    }
    var chooserIntent = Intent.createChooser(target, title)
    // Create a chooser from the main  intent
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && sender != null){
        chooserIntent  = Intent.createChooser(target, title,sender)
    }


    // Add all other intents
    chooserIntent.putExtra(
        Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())

    return chooserIntent
}

fun Activity.getCameraIntents(fileProvider: Boolean = false, authorityProvider: String? = null
): List<Intent> {

    val allIntents = ArrayList<Intent>()
    // Determine Uri of camera image to  save.
    val outputFileUri = getCameraImageUri(fileProvider,authorityProvider)

    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val listCam = packageManager.queryIntentActivities(captureIntent, 0)
    for (res in listCam) {
        val intent = Intent(captureIntent)
        intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
        intent.setPackage(res.activityInfo.packageName)
        if (outputFileUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        }
        allIntents.add(intent)
    }

    return allIntents
}

fun Activity.getPhotoIntents(action: String = Intent.ACTION_GET_CONTENT, includeDocuments: Boolean = true): List<Intent> {
    val intents = ArrayList<Intent>()
    val galleryIntent = if (action === Intent.ACTION_GET_CONTENT)
        Intent(action)
    else
    // if no intents found for get-content try pick intent action (Huawei P9)  ACTION_PICK.
        Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    galleryIntent.type = "image/*"
    val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
    for (res in listGallery) {
        val intent = Intent(galleryIntent)
        intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
        intent.setPackage(res.activityInfo.packageName)
        intents.add(intent)
    }

    // remove documents intent
    if (!includeDocuments) {
        for (intent in intents) {
            if (intent
                    .component!!
                    .className == "com.android.documentsui.DocumentsActivity") {
                intents.remove(intent)
                break
            }
        }
    }
    return intents
}


fun Activity.getImageUriResultFromIntent(data: Intent?, fileProvider: Boolean = false, authorityProvider: String? = null): Uri? {
    var isCamera = true
    if (data != null && data.data != null) {
        val action = data.action
        isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
    }
    return if (isCamera || data!!.data == null) getCameraImageUri(fileProvider,authorityProvider) else data.data
}

fun Activity.getCameraImageUri(fileProvider: Boolean = false, authorityProvider: String? = null): Uri? {
    return if( fileProvider && !authorityProvider.isNullOrEmpty() ) FileProvider.getUriForFile(this,authorityProvider,
        File(externalCacheDir, "pickImageResult.jpeg")
    )
    else Uri.fromFile(File(externalCacheDir, "pickImageResult.jpeg"))
}

fun Activity.getPhotoIntentQuery(action:String = Intent.ACTION_GET_CONTENT ): Intent {
    require(action ==  Intent.ACTION_GET_CONTENT || action == Intent.ACTION_PICK ){ " Action not valid "}
    val i =  if (action === Intent.ACTION_GET_CONTENT)
        Intent(action)
    else
    // if no intents found for get-content try pick intent action (Huawei P9)  ACTION_PICK.
        Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    i.type = "image/*"
    return  i
}

fun Activity.getCameraIntentQuery(fileProvider: Boolean = false, authorityProvider: String? = null): Intent {
    val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val  outputFileUri = getCameraImageUri(fileProvider,authorityProvider)
    if (outputFileUri != null) {
        i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
    }
    return  i
}

fun Activity.getGalleryIntent() : Intent?{
    var i = getPhotoIntentQuery()
    var g = packageManager.queryIntentActivities(i, 0)
    if(g.isEmpty()) {
        i = getPhotoIntentQuery(Intent.ACTION_PICK)
        g = packageManager.queryIntentActivities(i, 0)
    }
    g.forEach {
        if (it.activityInfo.packageName.contains("gallery")) {
            i.component = ComponentName(it.activityInfo.packageName, it.activityInfo.name)
        }
    }
    return i
}


fun Activity.getBitmapFromUri(path: Uri): Bitmap? {
    return  try {
        val inputStream = BufferedInputStream(contentResolver.openInputStream(path)!!)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        Log.e("JJKit","ERROR: $e")
        null
    }
}

fun Activity.getBitmapFromUriSampling(uri: Uri, reqWidth: Int, reqHeight:Int) : Bitmap? {
    return  try {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var inputStream = BufferedInputStream(contentResolver.openInputStream(uri)!!)
        BitmapFactory.decodeStream(inputStream,null,options)
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight)
        options.inJustDecodeBounds = false
        inputStream = BufferedInputStream(contentResolver.openInputStream(uri)!!)
        BitmapFactory.decodeStream(inputStream,null,options)
    } catch (e: Exception) {
        Log.e("JJKit","ERROR: $e")
        null
    }

}

fun Activity.calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
