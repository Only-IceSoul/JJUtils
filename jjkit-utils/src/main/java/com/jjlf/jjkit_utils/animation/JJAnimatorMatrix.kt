package com.jjlf.jjkit_utils.animation

import android.graphics.Matrix
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.ImageView
import com.jjlf.jjkit_utils.animation.interpolators.JJInterpolator
import com.jjlf.jjkit_utils.animation.interpolators.JJInterpolatorProvider


class JJAnimatorMatrix(target: ImageView? = null) : Animation() , Animation.AnimationListener {

    private var mImageView : ImageView? = target
    private var mStartImageMatrix = FloatArray(9)
    private var mEndImageMatrix = FloatArray(9)
    private var mAnimMatrix = FloatArray(9)

    init {
        duration = 300
        fillAfter = true
        interpolator = JJInterpolator(JJInterpolatorProvider.EASE_OUT_EXPO)
        setAnimationListener(this)
    }

    fun ssInterpolator(inter: Interpolator): JJAnimatorMatrix {
        interpolator = inter
        return this
    }

    fun ssDuration(durationMillis: Long) :JJAnimatorMatrix{
        duration = durationMillis
        return this
    }

    fun ssTarget(target: ImageView) :JJAnimatorMatrix {
        mImageView = target
        return this
    }

    fun ssStartState(imageMatrix: Matrix) {
        reset()
        imageMatrix.getValues(mStartImageMatrix)
    }

    fun ssEndState(imageMatrix: Matrix) {
        imageMatrix.getValues(mEndImageMatrix)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

        for (i in mAnimMatrix.indices) {
            mAnimMatrix[i] =
                mStartImageMatrix[i] + (mEndImageMatrix[i] - mStartImageMatrix[i]) * interpolatedTime
        }
        val m = mImageView?.imageMatrix
        m?.setValues(mAnimMatrix)
        mImageView?.imageMatrix = m
        mImageView?.invalidate()
    }

    override fun onAnimationEnd(animation: Animation?) {
        mImageView?.clearAnimation()
    }

    override fun onAnimationRepeat(animation: Animation?) {}
    override fun onAnimationStart(animation: Animation?) {}


}