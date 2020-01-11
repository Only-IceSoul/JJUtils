package com.jjlf.jjkit.extension

import android.graphics.Bitmap

fun Bitmap.sizeBytesInMap() : Long {
    if(android.os.Build.VERSION.SDK_INT >= 19){
        return byteCount + 200L
    }
    return (rowBytes * height) + 200L
}
fun String.sizeBytesInMap() : Long {
    return toByteArray(Charsets.UTF_16).size + 90L
}





