package com.jjlf.jjkit_utils.drawables

import android.graphics.*
import android.graphics.drawable.Drawable
import com.jjlf.jjkit_utils.extension.scale
import com.jjlf.jjkit_utils.extension.translation
import com.jjlf.jjkit_layoututils.JJScreen
import com.jjlf.jjkit_utils.extension.flipMirror
import com.jjlf.jjkit_utils.extension.rotation
import kotlin.math.min

class JJCircleProgress(size: Float = JJScreen.responsiveSize(7,6,5,4).toFloat()) : Drawable() {

    private val mPaintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = size
        strokeCap = Paint.Cap.ROUND
    }
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = size
        strokeCap = Paint.Cap.ROUND
    }

    private val mRect = RectF()
    private val mRectIdentity = RectF()
    private val mPathBg = Path()
    private val mPath = Path()
    private val mPathReverse = Path()
    private val mPathMeasure = PathMeasure()
    private var mProgress = 0f
    private val mMatrix = Matrix()

    private var mInset = 0f
    private var mDegrees = 0f
    private var mTransX = 0f
    private var mTransY = 0f
    private var mScale = 1f


    fun setColor(color: Int) : JJCircleProgress {
        mPaint.color = color
        invalidateSelf()
        return this
    }
    fun setBackColor(color:Int): JJCircleProgress {
        mPaintBg.color = color
        invalidateSelf()
        return this
    }
    fun setInset(inset: Float): JJCircleProgress {
        mInset = inset
        return this
    }

    fun setProgress(num: Float) : JJCircleProgress {
        mProgress = num
        invalidateSelf()
        return this
    }

    fun setCap(cap: Paint.Cap): JJCircleProgress {
        mPaintBg.strokeCap = cap
        mPaint.strokeCap = cap
        invalidateSelf()
        return this
    }

    fun setRotation(degrees:Float) : JJCircleProgress {
        mDegrees = degrees
        invalidateSelf()
        return this
    }
    fun setTranslation(dx:Float,dy:Float) : JJCircleProgress {
        mTransX = dx
        mTransY = dy
        invalidateSelf()
        return this
    }
    fun setScale(scale:Float) : JJCircleProgress {
        mScale = scale
        invalidateSelf()
        return this
    }

    fun getTranslationX(): Float { return mTransX  }
    fun getTranslationY(): Float { return mTransY  }
    fun getScale():Float { return mScale }
    fun getRotation():Float { return mDegrees }

    fun getPercent():Float{ return mProgress }
    fun getColor(): Int { return mPaint.color }
    fun getBackColor(): Int { return mPaintBg.color }
    fun getInset():Float { return mInset }
    fun getCap() : Paint.Cap { return mPaint.strokeCap }

    override fun onBoundsChange(bounds: Rect?) {
        if(bounds != null) {
            mRectIdentity.set(bounds)
            val inset = mPaint.strokeWidth/2 + mInset
            mRectIdentity.inset(inset,inset)
        }
    }

    override fun draw(canvas: Canvas) {
        mRect.set(mRectIdentity)
        mRect.translation(mTransX,mTransY,mMatrix)
        mRect.scale(mScale,mScale,mMatrix)

        val radius = min(mRect.width(),mRect.height()) / 2f
        mPathBg.reset()
        mPathBg.addRoundRect(mRect,radius,radius,Path.Direction.CW)
        mPathBg.rotation(mDegrees,mRect,mMatrix)
        mPathMeasure.setPath(mPathBg,false)

        mPath.reset()
        mPathMeasure.getSegment(0f,((mPathMeasure.length * mProgress) / 100f),mPath,true)
        mPath.rLineTo(0.0f, 0.0f)

        mPathReverse.reset()
        val ps = if(mProgress < 0f) -mProgress else 0f
        mPathMeasure.getSegment(0f,((mPathMeasure.length * ps) / 100f),mPathReverse,true)
        mPathReverse.rLineTo(0.0f, 0.0f)
        mPathReverse.flipMirror(vertical = false, horizontal = true, rect = mRect, ma = mMatrix)

        canvas.drawPath(mPathBg,mPaintBg)
        canvas.drawPath(mPath,mPaint)
        canvas.drawPath(mPathReverse,mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        mPaintBg.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }
}