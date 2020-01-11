package com.jjlf.jjkit_utils.extension

import android.graphics.Bitmap

@Suppress("DEPRECATION")
fun Bitmap.getBytesPerPixel(): Int = when (config) {
        Bitmap.Config.ARGB_8888 -> 4
        Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
        Bitmap.Config.ALPHA_8 -> 1
        else -> 1
    }