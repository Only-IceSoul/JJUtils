package com.jjlf.jjkit_utils.extension

import android.text.TextUtils
import android.widget.TextView

fun TextView.isEllipsized():Boolean{
    val truncateAt = this.ellipsize
    if (truncateAt == null || TextUtils.TruncateAt.MARQUEE == truncateAt) {
        return false
    }
    val layout = this.layout ?: return false
    for (line in 0 until layout.lineCount) {
        val num = layout.getEllipsisCount(line)
        if ( num > 0) {
            return true
        }
    }
    return false
}


//call when height and width has value (Activity.onAttachedToWindow or TextView.post)
fun TextView.isContentClippedHeight(): Int {
    val layout = this.layout ?: return -1
    val h = this.height - (this.paddingBottom + this.paddingTop)
    for (line in 0 until layout.lineCount) {
        val bottom = layout.getLineTop(line + 1)
        if ( bottom >= h) {
            return line
        }
    }
    return -1
}

fun TextView.isContentClippedWidth(): Int {
    val layout = this.layout ?: return -1
    val w = this.width - (this.paddingStart + this.paddingEnd)
    for (line in 0 until layout.lineCount) {
        val width = layout.getLineWidth(line)
        if ( width >= w) {
            return line
        }
    }
    return -1
}