package com.jjlf.jjkit_utils.animation.interpolators

import android.view.animation.Interpolator

class JJInterpolator(private val ease: Int) : Interpolator {


    override fun getInterpolation(input: Float): Float {
       return JJInterpolatorProvider.get(ease,input)
    }

     fun getEase(): Int {
        return ease
    }
}