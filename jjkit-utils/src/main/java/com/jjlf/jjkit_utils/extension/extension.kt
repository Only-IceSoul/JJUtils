package com.jjlf.jjkit_utils.extension

import java.lang.ref.WeakReference

fun <T> T.weak(): WeakReference<T> {
    return WeakReference(this)
}
