package com.jjlf.jjkit_utils.drawables

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.FloatRange
import com.jjlf.jjkit_layoututils.JJPadding
import com.jjlf.jjkit_utils.extension.padding
import com.jjlf.jjkit_utils.extension.scale
import com.jjlf.jjkit_utils.utils.PathParser
import com.jjlf.jjkit_utils.utils.ViewBox
import kotlin.math.min

class JJColorDrawable : Drawable() {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBaseRect = RectF()
    private val mRect = RectF()
    private var mIsStroke = false
    private var mIsFill = true
    private var mPadding = JJPadding()
    private var mShape = 0
    private var mRadius = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f)
    private var mPath = Path()
    private var mIsPath = false
    private var mSetupPath: ((RectF,Path)-> Unit)? = null
    private var mScaleX = -1f
    private var mScaleY = -1f
    private var mOffsetY = 0f
    private var mOffsetX = 0f
    private var mOffsetPlusY = 0f
    private var mOffsetPlusX = 0f
    private var mIsPathClosure = false
    private var mIsOffsetPercent = false

    private val mPaintStrokeShadow = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mStrokeShadowRadius = 4f
    private var mIsStrokeShadow = false
    private var mStrokeShadowClosure:((RectF, Path)->Unit)?=null
    private var mPathStrokeShadow = Path()
    private var mRectStrokeShadow = RectF()

    companion object{
        const val ROUND_CIRCLE = 1
        const val CORNER_SMOOTH_VERY_SMALL = 2
        const val CORNER_SMOOTH_SMALL = 3
        const val CORNER_SMOOTH_MEDIUM = 4
        const val CORNER_SMOOTH_LARGE = 5
        const val CORNER_SMOOTH_XLARGE = 6
    }

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.WHITE
        mPaintStroke.style = Paint.Style.STROKE

    }

    //effect where fill can be transparent with a stroke shadow
    fun setStrokeShadow(@FloatRange(from = 0.0 ,to = 7.0) radius: Float,color: Int, shadowPath: ((RectF,Path)-> Unit)?): JJColorDrawable {
        mStrokeShadowRadius = radius
        mPaintStrokeShadow.style = Paint.Style.STROKE
        mPaintStrokeShadow.strokeWidth = 0.5f
        mPaintStrokeShadow.setShadowLayer(mStrokeShadowRadius,0f,0f,color)
        mStrokeShadowClosure = shadowPath
        mIsStrokeShadow = true
        mIsStroke = false
        return this
    }

    fun setScale(x: Float,y: Float): JJColorDrawable {
        mScaleX = x
        mScaleY = y
        return this
    }

    fun setShape(type:Int): JJColorDrawable {
        mIsPath = false
        mIsPathClosure = false
        mShape = type
        return this
    }
     //normal stroke this is a new layer
    fun setStroke(width: Float, color: Int) : JJColorDrawable {
        mIsStroke = width >= 1f
        mPaintStroke.strokeWidth = width
        mPaintStroke.color = color
        mIsStrokeShadow = false
        return this
    }

    //color for stroke new layer
    fun setStrokeColor(color:Int) : JJColorDrawable {
        mPaintStroke.color = color
        return this
    }

    //new layer stroke plus shadow layer
    fun setStrokeAndShadowLayer(width: Float, color: Int,shadowRadius: Float,shadowOffsetX:Float,shadowOffsetY:Float,shadowColor:Int) : JJColorDrawable {
        mIsStroke = width >= 1f
        mPaintStroke.strokeWidth = width
        mPaintStroke.color = color
        mPaintStroke.setShadowLayer(shadowRadius,shadowOffsetX,shadowOffsetY,shadowColor)
        mIsStrokeShadow = false
        return this
    }

    fun setRadius(radius: Float) : JJColorDrawable {
        mRadius = floatArrayOf(radius,radius,radius,radius,radius,radius,radius,radius)
        mIsPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setRadius(radius: FloatArray) : JJColorDrawable {
        mRadius = radius
        mIsPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setRadius(topLeft: Float,topRight:Float,bottomRight:Float,bottomLeft:Float) : JJColorDrawable {
        mRadius = floatArrayOf(topLeft,topLeft,topRight,topRight,bottomRight,bottomRight,bottomLeft,bottomLeft)
        mIsPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setFillColor(color: Int) : JJColorDrawable {
        mPaint.color = color
        return this
    }

    fun setIsFillEnabled(boolean: Boolean) : JJColorDrawable {
        mIsFill = boolean
        return this
    }

    fun setIsStrokeEnabled(boolean: Boolean) : JJColorDrawable {
        mIsStroke = boolean
        return this
    }

    fun setIsStrokeShadowEnabled(boolean: Boolean) : JJColorDrawable {
        mIsStrokeShadow = boolean
        return this
    }
    fun setPadding(padding: JJPadding): JJColorDrawable {
        mPadding = padding
        return this
    }

    fun setOffset(x: Float,y: Float,percent:Boolean = false): JJColorDrawable {
        mOffsetX = x
        mOffsetY = y
        mIsOffsetPercent = percent
        return this
    }

    fun setOffset(percentX: Float,percentY: Float,plusX:Float,plusY:Float): JJColorDrawable {
        mOffsetX = percentX
        mOffsetY = percentY
        mIsOffsetPercent = true
        mOffsetPlusX  = plusX
        mOffsetPlusY = plusY
        return this
    }

    fun setPath(path:Path): JJColorDrawable {
        mIsPath = true
        mIsPathClosure = false
        mPath = path
        mShape = 0
        return this
    }

    private val mVbRect = RectF()
    private var mIsSvgPath = false
    private var mMeetOrSlice = 0
    private var mAlign = "xMidYMid"
    private val mVbMatrix = Matrix()
    private var mSvgPathD = ""
    private var mDensity = 1f
    fun setSvgPath(d:String,vbBox:FloatArray,meetOrSlice:Int,align:String,density:Float): JJColorDrawable {
        if(vbBox.size < 4) return this
        mIsPath = true
        mIsPathClosure = false
        mIsSvgPath = true
        mSvgPathD = d
        mVbRect.set(vbBox[0] * density ,vbBox[1] * density,(vbBox[0] + vbBox[2]) * density ,(vbBox[1] + vbBox[3]) * density)
        mAlign = align
        mMeetOrSlice = meetOrSlice
        mDensity = density
        mShape = 0
        return this
    }

    fun setPath(closure:(RectF, Path)->Unit): JJColorDrawable {
        mIsPath = true
        mIsPathClosure = true
        mSetupPath = closure
        mShape = 0
        return this
    }
    fun diposePath(): JJColorDrawable {
        mIsPath = false
        mIsPathClosure = false
        mIsSvgPath = false
        mPath.reset()
        mSetupPath = null
        return this
    }

    fun diposeStrokeShadow(): JJColorDrawable {
        mIsStrokeShadow = false
        mPathStrokeShadow.reset()
        mStrokeShadowClosure = null
        return this
    }



    override fun onBoundsChange(bounds: Rect?) {
        if(bounds != null){
            mBaseRect.set(bounds)
        }
    }

    override fun draw(canvas: Canvas) {
        mRect.set(mBaseRect)
        if(mRect.width() > 0f && mRect.height() >0f) {
            setupRect()
            if(mIsStrokeShadow) {
                mPathStrokeShadow.reset()
                mRectStrokeShadow.set(mRect)
                mRectStrokeShadow.inset(mStrokeShadowRadius,mStrokeShadowRadius)
                mStrokeShadowClosure?.invoke(mRectStrokeShadow,mPathStrokeShadow)
                mRect.inset(mStrokeShadowRadius+0.25f,mStrokeShadowRadius+0.25f)
                mPaintStrokeShadow.color = if(mIsFill && mPaint.color != 0) mPaint.color else Color.parseColor("#80D1D1D1")
            }


            if(mIsPathClosure  && mIsPath){
                mPath.reset()
                mSetupPath?.invoke(mRect, mPath)
            }

            if(mIsSvgPath  && mIsPath){
                mPath.reset()
                ViewBox.transform(mVbRect,mRect,mAlign,mMeetOrSlice,mVbMatrix)
                PathParser.mScale = mDensity
                val p = PathParser.parse(mSvgPathD)
                mPath.set(p)
                mPath.transform(mVbMatrix)

            }


            if (!mIsPath) {
                mPath.reset()
                mPathStrokeShadow.reset()
                setupRadiusForShape()
                mPath.addRoundRect(mRect, mRadius, Path.Direction.CW)
                mPathStrokeShadow.addRoundRect(mRectStrokeShadow, mRadius, Path.Direction.CW)
            }

            if(mIsStrokeShadow) canvas.drawPath(mPathStrokeShadow, mPaintStrokeShadow)
            if (mIsFill) canvas.drawPath(mPath, mPaint)
            if (mIsStroke) canvas.drawPath(mPath, mPaintStroke)

        }
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        mPaintStroke.alpha = alpha
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaintStroke.colorFilter = colorFilter
        mPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    private var mMatrix = Matrix()
    private fun setupRect(){
        mMatrix.reset()
        if(mIsStroke) {
            val inset = mPaintStroke.strokeWidth / 2f
            mRect.inset(inset,inset)
        }
        mRect.padding(mPadding)
        mRect.scale(mScaleX, mScaleY,mMatrix)

        if(mIsOffsetPercent){
            mOffsetX = if(mOffsetX < 0f) 0f else if (mOffsetX > 1f) 1f else mOffsetX
            mOffsetY = if(mOffsetY < 0f) 0f else if (mOffsetY > 1f) 1f else mOffsetY
            val ox = mOffsetX * mBaseRect.width()
            val oy = mOffsetY * mBaseRect.height()
            mRect.offset(ox + mOffsetPlusX, oy + mOffsetPlusY)
        }else{
            mRect.offset(mOffsetX, mOffsetY)
        }

    }

    private fun setupRadiusForShape(){
        when(mShape){
            ROUND_CIRCLE -> {
                val radius = min(mRect.height(),mRect.width()) / 2f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            2 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.19f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            3 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.04f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            4 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.1f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }

            5 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.14f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }

            6 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.2f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            else -> Log.v("JJColorDrawable","Custom Radius")
        }
    }

}