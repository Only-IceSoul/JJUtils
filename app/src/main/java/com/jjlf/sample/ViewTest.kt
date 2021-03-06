package com.jjlf.sample

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

import androidx.constraintlayout.widget.ConstraintLayout
import com.jjlf.jjkit_utils.extension.scale
import com.jjlf.jjkit_utils.utils.PathParser
import com.jjlf.jjkit_utils.utils.ViewBox

class ViewTest : View {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintBmp = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPath = Path()
    private val mVbMatrix = Matrix()
    private val mRect = RectF()
    private val mVbRect = RectF()
    private val mSvgPathD = "M45.09,10.69a29,29,0,0,0-29,29s-1.39,12.88,11.11,26.4V89.37H59.62v-12h10.3a6.25,6.25,0,0,0,6.25-6.25V60.71c2.3-.52,8.05-2.68,7.83-4.56-.25-2.15-6.57-16.42-6.57-16.42C71.87,7.91,45.09,10.69,45.09,10.69Z"
    private var mCanvas: Canvas? = null
    private var mBitmap: Bitmap? = null
    private var mDensity = 1f
    constructor(context:Context):super(context){
        mDensity = context.resources.displayMetrics.density

        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.RED
//        clipChildren = false
//        mPaint.setShadowLayer(30f,0f,0f,Color.parseColor("#8000FF00"))

    }
    constructor(context: Context,attrs: AttributeSet):super(context,attrs){
        mDensity = context.resources.displayMetrics.density
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.RED

//        mPaint.setShadowLayer(30f,0f,0f,Color.RED)


    }


    val ma = Matrix()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mRect.set(0f,0f,width.toFloat(),height.toFloat())
        mPaintBmp.style = Paint.Style.FILL
        mPaintBmp.color = Color.WHITE
        canvas.drawRect(mRect,mPaintBmp)
        mPath.reset()
        mPath.moveTo(150f,100f)

        mRect.scale(0.5f,0.5f,ma)
//        mPath.addRoundRect(mRect,width * 0.25f,width * 0.25f,Path.Direction.CW)
        mPath.lineTo(150f,150f)
        mPath.lineTo(130f,180f)
        mPath.lineTo(20f,20f)
        mPath.moveTo(200f,200f)
        mPath.rLineTo(40f,0f)
        mPath.rLineTo(0f,40f)
        mPath.close()
        canvas.drawPath(mPath,mPaint)
    }

    override fun dispatchDraw(canvas: Canvas) {
//        if(mCanvas == null ||  mBitmap == null){
//            mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
//            mCanvas = Canvas(mBitmap!!)
//        }
//
//        mRect.set(0f,0f,width.toFloat(),height.toFloat())
//        mVbRect.set(0f * mDensity,0f * mDensity,100f * mDensity,125f * mDensity)
//        mPath.reset()
//        mPath.addRect(mRect,Path.Direction.CW)
//        ViewBox.transform(mVbRect,mRect,"xMidYMid",0,mVbMatrix)
//        PathParser.mScale = mDensity
////        val p = PathParser.parse(mSvgPathD)
////        mPath.set(p)
////        mPath.transform(mVbMatrix)
//        mPath.addArc(mRect,-90f,180f)

//        canvas.drawPath(mPath, mPaint)


//        canvas.drawPath(mPath,mPaint)

        super.dispatchDraw(canvas)





    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        Log.e("ICESOUL","draw")
    }
}