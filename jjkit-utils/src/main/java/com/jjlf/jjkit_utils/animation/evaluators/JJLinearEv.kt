package com.jjlf.jjkit_utils.animation.evaluators

class JJLinearEv(duration: Float) : JJBaseEasingEvaluator(duration) {


   override fun calculate(t: Float, b: Float, c: Float, d: Float): Float {
        return c * t / d + b
    }
}
