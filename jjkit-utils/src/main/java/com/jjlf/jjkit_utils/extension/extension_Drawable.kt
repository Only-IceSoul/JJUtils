package com.jjlf.jjkit_utils.extension

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable

fun Drawable.setColorFilterCompact(color:Int, mode: PorterDuff.Mode){
    colorFilter = if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
        BlendModeColorFilter(color, BlendMode.valueOf(mode.name))
    }else{
        PorterDuffColorFilter(color,mode)
    }
}