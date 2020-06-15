package com.jjlf.jjkit_utils.extension

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat

fun ImageView.setImageTintListCompat(colorStateList:ColorStateList?){
    ImageViewCompat.setImageTintList(this,colorStateList)
}

fun ImageView.setImageTintModeCompat(mode:PorterDuff.Mode?){

    ImageViewCompat.setImageTintMode(this,mode)

}
fun ImageView.getImageTintListCompat() : ColorStateList?{
    return ImageViewCompat.getImageTintList(this)
}
fun ImageView.getImageTintModeCompat() : PorterDuff.Mode?{
    return ImageViewCompat.getImageTintMode(this)
}