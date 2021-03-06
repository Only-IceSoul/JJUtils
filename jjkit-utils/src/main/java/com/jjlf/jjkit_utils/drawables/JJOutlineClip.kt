package com.jjlf.jjkit_utils.drawables

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.RectF
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.graphics.toRect
import com.jjlf.jjkit_utils.extension.translation

class JJOutlineClip : ViewOutlineProvider() {

    private var mProgress = 0f
    private var mGravity = Gravity.BOTTOM
    private var mInset = 0f
    private var mRect = RectF()
    private var mMatrix = Matrix()

    fun setGravity(gravity: Int) : JJOutlineClip {
        mGravity = gravity
        return this
    }
    fun setProgress(num: Float) {
        mProgress = num
    }
    fun setInset(inset: Float): JJOutlineClip {
        mInset = inset
        return this
    }

    fun getProgress() :Float { return mProgress }
    fun getInset() :Float { return mInset }
    fun getGravity() :Int { return mGravity }

    @SuppressLint("RtlHardcoded")
    override fun getOutline(view: View?, outline: Outline?) {
        if(view != null && outline != null) {
            mRect.set(0f,0f,view.width.toFloat(),view.height.toFloat())
            mRect.inset(mInset,mInset)
            when (mGravity) {
                Gravity.TOP -> makeTop(view,outline)
                Gravity.BOTTOM -> makeBottom(view, outline)
                Gravity.LEFT, Gravity.START -> makeStart(view,outline)
                Gravity.RIGHT, Gravity.END -> makeEnd(view,outline)
                else -> makeBottom(view, outline)
            }
        }
    }
    private fun makeBottom(view:View,outline: Outline){
        var h = mRect.height()
          h = if(h < 0f) 0f else h
        val dy = h - (mProgress / 100f) * h
        mRect.translation(0f,dy,mMatrix)
         outline.setRect(mRect.toRect())
    }
    private fun makeTop(view:View,outline: Outline){
        var h = mRect.height()
        h = if(h < 0f) 0f else h
        val dy = -(h - (mProgress / 100) * h)
        mRect.translation(0f,dy,mMatrix)
        outline.setRect(mRect.toRect())
    }

    private fun makeStart(view:View,outline: Outline){
        var w = mRect.width()
        w = if(w < 0f) 0f else w
        val dx = -(w - (mProgress / 100) * w)
        mRect.translation(dx,0f,mMatrix)
        outline.setRect(mRect.toRect())
    }
    private fun makeEnd(view:View,outline: Outline){
        var w = mRect.width()
        w = if(w < 0f) 0f else w
        val dx = w - (mProgress / 100) * w

        mRect.translation(dx,0f,mMatrix)
        outline.setRect(mRect.toRect())
    }




}