package com.jjlf.jjkit_utils.extension

import kotlin.math.PI


fun Float.clampNotNegative() : Float {
    return if(this > 0f) this else 0f
}

  fun Float.clamp(min:Float = 0f, max:Float = 1f) : Float {
    return if(this < min) min else if(this > max) max else this
}

fun Float.toRadians() : Float {
    return this * PI.toFloat() / 180f
}
