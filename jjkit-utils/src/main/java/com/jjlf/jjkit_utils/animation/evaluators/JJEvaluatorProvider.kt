package com.jjlf.jjkit_utils.animation.evaluators

import android.animation.TypeEvaluator

object JJEvaluatorProvider {

    const val Linear = 0
    const val BackEaseIn = 1
    const val BackEaseOut = 2
    const val BackEaseInOut = 3

    const val BounceEaseIn = 4
    const val BounceEaseOut = 5
    const val BounceEaseInOut = 6

    const val CircEaseIn = 7
    const val CircEaseOut = 8
    const val CircEaseInOut = 9

    const val CubicEaseIn = 10
    const val CubicEaseOut = 11
    const val CubicEaseInOut = 12

    const val ElasticEaseIn = 13
    const val ElasticEaseOut = 14

    const val ExpoEaseIn = 15
    const val ExpoEaseOut = 16
    const val ExpoEaseInOut = 17

    const val QuadEaseIn = 18
    const val QuadEaseOut = 19
    const val QuadEaseInOut = 20

    const val QuintEaseIn = 21
    const val QuintEaseOut = 22
    const val QuintEaseInOut = 23

    const val SineEaseIn = 24
    const val SineEaseOut = 25
    const val SineEaseInOut = 26


    fun get(ease:Int,duration:Float): TypeEvaluator<Number> = when(ease){
        Linear -> JJLinearEv(duration)
        BackEaseOut -> JJBackEaseOutEv(duration)

        else ->  JJLinearEv(duration)
    }




}