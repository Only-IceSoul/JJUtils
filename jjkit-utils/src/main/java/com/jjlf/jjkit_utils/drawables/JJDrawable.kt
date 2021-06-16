package com.jjlf.jjkit_utils.drawables

import android.graphics.*
import android.graphics.drawable.Drawable
import android.renderscript.Matrix4f
import android.util.Log
import androidx.core.graphics.withMatrix
import com.jjlf.jjkit_utils.extension.clamp
import com.jjlf.jjkit_utils.utils.PathParser
import com.jjlf.jjkit_utils.utils.ViewBox
import kotlin.math.min

class JJDrawable : Drawable() {
    
    companion object{
        const val NONE = 0
        const val CIRCLE = 1
        const val SVG_PATH = 2
        const val RADIUS_RELATIVE_WIDTH = 3
        const val RADIUS_RELATIVE_HEIGHT = 4
        const val VIEW_BOX_MEET = 0
        const val VIEW_BOX_SLICE = 1
        const val VIEW_BOX_NONE = 2
        const val AXIS_X = 1
        const val AXIS_Y = 2
        const val AXIS_Z = 3
    }
    
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintBg = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mRotationX  = 0f
    private var mRotationY  = 0f
    private var mRotationZ  = 0f
    private var mRotationOrder = intArrayOf(AXIS_Z, AXIS_X, AXIS_Y)
    private var mTranslationX  = 0f
    private var mTranslationY  = 0f
    private var mTranslationPlusX  = 0f
    private var mTranslationPlusY  = 0f
    private var mIsTranslationPercent  = false
    private var mScaleX   = 1f
    private var mScaleY   = 1f
    private var mBaseRect = RectF()
    private var mRect = RectF()
    private var mRadius  = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f)
    private var mShape = NONE
    private var mSvgPath = ""
    private var mSvgAlign = "none"
    private var mSvgAspect = VIEW_BOX_NONE
    private var mVbRect = RectF()
    private val mVbMatrix = Matrix()
    private val mCanvasMatrix = Matrix()
    private var mDensity = 1f
    private val mPathMeasure = PathMeasure()

    //MAR: STROKE start and end

    private var mStrokeStart = 0f
    private var mStrokeEnd = 1f

    //MARK : SHADOW PROPS
    private var mShadowOffsetX = 0f
    private var mShadowOffsetY = 0f
    private var mShadowRadius = 2f
    private var mShadowOpacity = 0f
    private var mShadowColor = Color.BLACK

    //MARK: Path PROPS
    private var mPath  = Path()
    private var mPathStroke = Path()
    private var mPathScaleX = 1f
    private var mPathScaleY = 1f
    private var mPathTranslationX = 0f
    private var mPathTranslationY = 0f
    private var mPathRotation = 0f
    private var mIsPathTranslationPercent = false
    private var mPathTranslationPlusX = 0f
    private var mPathTranslationPlusY = 0f
    private var mPathMatrix = Matrix()
    private var mPathBounds = RectF()

    private var mBlurRadius = 0f
    private var mBorderBlurRadius = 0f
    private var mBgBlurRadius = 0f

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.TRANSPARENT
        mPaint.strokeWidth = 0f
        mPaintBg.style = Paint.Style.FILL
        mPaintBg.color = Color.TRANSPARENT
        mPaintStroke.style = Paint.Style.STROKE
        mPaintStroke.strokeWidth = 0f
        mPaintStroke.color = Color.BLACK

    }

    fun setBlur(radius:Float): JJDrawable {
        mBlurRadius = radius
        mBorderBlurRadius = radius
        mBgBlurRadius = radius
        return this
    }
    fun setBackgroundBlur(radius:Float): JJDrawable {
        mBgBlurRadius = radius
        return this
    }
    fun setFillBlur(radius:Float): JJDrawable {
        mBlurRadius = radius
        return this
    }
    fun setBorderBlur(radius:Float): JJDrawable {
        mBorderBlurRadius = radius
        return this
    }

    //MARK: Path set and get
    fun setPathScale(sx:Float,sy:Float): JJDrawable {
        mPathScaleX = sx
        mPathScaleY = sy
        return this
    }
    
    fun setPathRotation(degrees: Float) : JJDrawable {
        mPathRotation = degrees
        return this
    }
    //pixels
    fun setPathTranslation(dx:Float,dy:Float): JJDrawable {
        mIsPathTranslationPercent = false
        mPathTranslationX = dx
        mPathTranslationY = dy
        return this
    }
  
    fun setPathTranslation(percentX:Float,percentY:Float, plusX:Float, plusY:Float): JJDrawable {
        mPathTranslationX = percentX
        mPathTranslationY = percentY
        mPathTranslationPlusX = plusX
        mPathTranslationPlusY = plusY
        mIsPathTranslationPercent = true
        return this
    }

    //MARK: SHADOW SET

    fun setShadowOffset(x:Float,y:Float) : JJDrawable {
        mShadowOffsetX = x
        mShadowOffsetY = y
        return this
    }
    
    fun setShadowRadius(r:Float) : JJDrawable {
        mShadowRadius = r
        return this
    }
    
    fun setShadowOpacity(o:Float) : JJDrawable {
        mShadowOpacity = o.clamp()
        return this
    }
    
    fun setShadowColor(c:Int) : JJDrawable {
        mShadowColor = c
        return this
    }

    //MARK: STROKE SET
    fun setStrokeWidth(w:Float) : JJDrawable {
        mPaint.strokeWidth = w
        mPaintStroke.strokeWidth = w
        return this
    }
    
    fun setStrokeColor(color:Int) : JJDrawable {
        mPaintStroke.color = color
        return this
    }
    fun setStrokeStart(s:Float) : JJDrawable {
       mStrokeStart = s.clamp()
        return this
    }

    fun setStrokeEnd(e:Float) : JJDrawable {
        mStrokeEnd = e.clamp()
        return this
    }



    //MARK: LAYER SET

    fun setSvgPath(d:String,density:Float,viewBox:FloatArray = floatArrayOf(0f,0f,0f,0f),align:String = "none",aspect:Int = 2) : JJDrawable {
        mSvgPath = d
        mSvgAlign = align
        mSvgAspect = aspect
        mDensity = density
        mVbRect.set(viewBox[0] * density ,viewBox[1] * density,(viewBox[0] + viewBox[2]) * density ,(viewBox[1] + viewBox[3]) * density)
        return this
    }
    
    fun setShape(s:Int) : JJDrawable {
        mShape = s
        return this
    }
    
    fun setRadius(v:Float) : JJDrawable {
        mRadius = floatArrayOf(v,v,v,v,v,v,v,v)
        return this
    }

    fun setRadius(topLeft:Float,topRight:Float,bottomLeft:Float,bottomRight:Float) : JJDrawable {
        mRadius = floatArrayOf(topLeft,topLeft,topRight,topRight,bottomRight,bottomRight,bottomLeft,bottomLeft)
        return this
    }
    
    fun setFillColor(c:Int) : JJDrawable {
        mPaint.color = c
        return this
    }

    fun setBackgroundColor(c:Int):JJDrawable{
        mPaintBg.color = c
        return this
    }


    //MARK: layer set transform

    fun setScale(sx:Float,sy:Float): JJDrawable{
        mScaleX = sx
        mScaleY = sy
        return this
    }
   
    fun setRotationZ(degrees: Float) : JJDrawable{
        mRotationZ = degrees

        return this
    }
    fun setRotationX(degrees: Float) : JJDrawable{
        mRotationX = degrees
        return this
    }
    fun setRotationY(degrees: Float) : JJDrawable{
        mRotationY = degrees
        return this
    }
    fun setRotationOrder(f: Int,s:Int,t:Int) : JJDrawable{
        if(f !in 1..3){ return this }
        if(t !in 1..3){ return this }
        if(s !in 1..3){ return this }
        if((f == s || f == t) || (s == t)){ return this }
        mRotationOrder = intArrayOf(f,s,t)
        return this
    }
    //pixels
    fun setTranslation(dx:Float,dy:Float): JJDrawable {
        mIsTranslationPercent = false
        mTranslationX = dx
        mTranslationY = dy
        return this
    }
   
    fun setTranslation(percentX:Float,percentY:Float, plusX:Float = 0f,plusY:Float = 0f): JJDrawable {
        mTranslationX = percentX
        mTranslationY = percentY
        mTranslationPlusX = plusX
        mTranslationPlusY = plusY
        mIsTranslationPercent = true
        return this
    }
   

    private var mBoundsX = 0f
    private var mBoundsY = 0f
    private var mBoundsWidth = 0f
    private var mBoundsHeight = 0f
    private var mIsBoundsDynamically = false
    private var mIsBoundsPercentPos = false
    private var mIsBoundsPercentSize = false
    fun setBoundsDynamically(x:Float,y:Float,width:Float,height:Float,percentPos:Boolean = false,percentSize:Boolean = false) : JJDrawable{
        mIsBoundsDynamically = true
        mIsBoundsPercentPos = percentPos
        mIsBoundsPercentSize = percentSize
        mBoundsX = x
        mBoundsY = y
        mBoundsWidth = width
        mBoundsHeight = height
         return this
    }

    private var mBlurRect = RectF()
    //MARK: DRAWABLE METHODS
    override fun onBoundsChange(bounds: Rect?) {
        bounds?.let{

            if(mIsBoundsPercentPos){
                mBoundsX *= bounds.width()
                mBoundsY *= bounds.height()
            }
            if(mIsBoundsPercentSize){
                mBoundsWidth *= bounds.width()
                mBoundsHeight *= bounds.height()
            }
            if(mIsBoundsDynamically){
                mBaseRect.set(mBoundsX,mBoundsY,mBoundsX+mBoundsWidth,mBoundsY+mBoundsHeight)
            }else{
                mBaseRect.set(it)
            }

        }
    }



    override fun draw(canvas: Canvas) {
        if(bounds.width() > 0f && bounds.height() >0f) {

            setupRect()
            setupPath()

            setupCanvasMatrix()

            canvas.withMatrix(mCanvasMatrix){

                if(mPaintBg.color != Color.TRANSPARENT) {
                    mBlurRect.set(mBaseRect)
                    mPaintBg.maskFilter =  if(mBgBlurRadius > 0f) BlurMaskFilter(mBgBlurRadius,BlurMaskFilter.Blur.NORMAL) else null
                    if(mBlurRadius > 0f ){
                        mBlurRect.inset(mBgBlurRadius,mBgBlurRadius)
                    }
                    drawRect(mBlurRect,mPaintBg)
                }

                if (mPaint.color != Color.TRANSPARENT || mShadowOpacity > 0f) {
                    if(mShadowOpacity > 0f){
                        val alpha = Color.alpha(mShadowColor)
                        val red = Color.red(mShadowColor)
                        val green = Color.green(mShadowColor)
                        val blue = Color.blue(mShadowColor)
                        val c = Color.argb((mShadowOpacity * alpha).toInt(),red,green,blue)
                        mPaint.setShadowLayer(mShadowRadius,mShadowOffsetX,mShadowOffsetY,c)
                    }else{
                        mPaint.clearShadowLayer()
                    }
                    mPaint.maskFilter =  if(mBlurRadius > 0f) BlurMaskFilter(mBlurRadius,BlurMaskFilter.Blur.NORMAL) else null
                    drawPath(mPath, mPaint)
                }
                if (mPaintStroke.color != Color.TRANSPARENT && mPaintStroke.strokeWidth > 0f && mStrokeStart < mStrokeEnd) {
                    mPaintStroke.maskFilter =  if(mBorderBlurRadius > 0f) BlurMaskFilter(mBorderBlurRadius,BlurMaskFilter.Blur.NORMAL) else null
                    if(mStrokeStart != 0f || mStrokeEnd != 1f){
                        mPathStroke.reset()
                        mPathMeasure.setPath(mPath,false)
                        mPathMeasure.getSegment((mPathMeasure.length * mStrokeStart),(mPathMeasure.length * mStrokeEnd),mPathStroke,true)
                        mPathStroke.rLineTo(0f,0f)
                        drawPath(mPathStroke, mPaintStroke)
                    }else{
                        drawPath(mPath, mPaintStroke)
                    }

                }


            }
        }
    }
    private val mCamera = Camera()
    private fun setupCanvasMatrix(){
        var transX = mTranslationX
        var transY = mTranslationY
        if(mIsTranslationPercent){
            transX = (mTranslationX * bounds.width()) + mTranslationPlusX
            transY = (mTranslationY * bounds.height()) + mTranslationPlusY
        }

        mCanvasMatrix.reset()
        mCamera.save()

        for(e in mRotationOrder){
            if(e == AXIS_Z){
                mCamera.rotate(0f,0f, -mRotationZ)
            }
            if(e == AXIS_X){
                mCamera.rotate(-mRotationX,0f, 0f)
            }
            if(e == AXIS_Y){
                mCamera.rotate(0f,-mRotationY, 0f)
            }
        }

        mCamera.getMatrix(mCanvasMatrix)
        mCamera.restore()

        mCanvasMatrix.postScale(mScaleX,mScaleY)
        mCanvasMatrix.preTranslate(-mBaseRect.centerX(),-mBaseRect.centerY())
        mCanvasMatrix.postTranslate(mBaseRect.centerX(),mBaseRect.centerY())
        mCanvasMatrix.postTranslate(transX,transY)


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


    private fun setupRect(){
        mRect.set(mBaseRect)
        val strokeInset = mPaintStroke.strokeWidth / 2f
        mRect.inset( strokeInset, strokeInset)

    }

    private fun setupPath(){
        mPath.reset()
        when(mShape){
            CIRCLE -> {
                val radius = min(mRect.height(),mRect.width()) / 2f
                for (i in 0..7){
                    mRadius[i] = radius
                }
                mPath.addRoundRect(mRect,mRadius,Path.Direction.CW)
            }
            RADIUS_RELATIVE_HEIGHT, RADIUS_RELATIVE_WIDTH -> {
                val tl = mRadius[0].clamp()
                val tr = mRadius[2].clamp()
                val br = mRadius[4].clamp()
                val bl = mRadius[6].clamp()
                val size = if(mShape == RADIUS_RELATIVE_WIDTH ) mRect.width() else mRect.height()
                mRadius[0] = tl * size
                mRadius[1] = tl * size
                mRadius[2] = tr * size
                mRadius[3] = tr * size
                mRadius[4] = br * size
                mRadius[5] = br * size
                mRadius[6] = bl * size
                mRadius[7] = bl * size
                mPath.addRoundRect(mRect,mRadius,Path.Direction.CW)
            }
            SVG_PATH -> {
                PathParser.mScale = mDensity
                val p = PathParser.parse(mSvgPath)
                mPath.set(p)
                if (mVbRect.width() > 0f && mVbRect.height() > 0f){
                    ViewBox.transform(mVbRect,mRect,mSvgAlign,mSvgAspect,mVbMatrix)
                    mPath.transform(mVbMatrix)
                }
            }
            else -> {
                mPath.addRoundRect(mRect,mRadius,Path.Direction.CW)
            }

        }


        //path transform
        var transX = mPathTranslationX
        var transY = mPathTranslationY
        if(mIsPathTranslationPercent){
            transX = (mPathTranslationX * mBaseRect.width()) + mPathTranslationPlusX
            transY = (mPathTranslationY * mBaseRect.height()) + mPathTranslationPlusY
        }
        mPathBounds.set(0f,0f,0f,0f)
        mPath.computeBounds(mPathBounds,true)
        mPathMatrix.reset()
        mPathMatrix.postRotate(mPathRotation,mPathBounds.centerX(),mPathBounds.centerY())
        mPathMatrix.postScale(mPathScaleX,mPathScaleY,mPathBounds.centerX(),mPathBounds.centerY())
        mPathMatrix.postTranslate(transX,transY)
        mPath.transform(mPathMatrix)
    }

}