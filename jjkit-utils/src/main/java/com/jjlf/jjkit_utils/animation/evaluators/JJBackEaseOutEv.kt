package com.jjlf.jjkit_utils.animation.evaluators

class JJBackEaseOutEv : JJBaseEasingEvaluator {

    private var s = 1.70158f

    constructor(duration: Float)  : super(duration)

    constructor(duration: Float, back: Float):super(duration){ s = back }

   override fun calculate(t: Float, b: Float, c: Float, d: Float): Float {
        var tt = t
        return c * ((tt / d - 1.also { tt = it.toFloat() }) * tt * ((s + 1) * tt + s) + 1) + b
    }
}