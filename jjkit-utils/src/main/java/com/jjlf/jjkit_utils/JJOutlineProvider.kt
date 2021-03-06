package com.jjlf.jjkit_utils

import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.core.graphics.toRect
import com.jjlf.jjkit_layoututils.JJPadding
import com.jjlf.jjkit_utils.drawables.JJColorDrawable
import com.jjlf.jjkit_utils.extension.padding
import com.jjlf.jjkit_utils.extension.scale
import com.jjlf.jjkit_utils.utils.PathParser
import com.jjlf.jjkit_utils.utils.ViewBox

import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class JJOutlineProvider : ViewOutlineProvider() {
    private var mRadius = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f)
    private var mScaleX = -1f
    private var mScaleY = -1f
    private var mOffsetY = 0f
    private var mOffsetX = 0f
    private var mOffsetPlusY = 0f
    private var mOffsetPlusX = 0f
    private var mAlpha = 0.99f
    private var mPath = Path()
    private val mRect = RectF()
    private var mShape = 0
    private var mIsPathClosure = false
    private var mSetupPath: ((RectF,Path)-> Unit)? = null
    private var mIsPath = false
    private var mPadding = JJPadding()
    private var mIsOffsetPercent = false

    companion object{
        const val ROUND_CIRCLE = 1
        const val CORNER_SMOOTH_VERY_SMALL = 2
        const val CORNER_SMOOTH_SMALL = 3
        const val CORNER_SMOOTH_MEDIUM = 4
        const val CORNER_SMOOTH_LARGE = 5
        const val CORNER_SMOOTH_XLARGE = 6
    }

    fun setScale(x: Float,y: Float):JJOutlineProvider{
        mScaleX = x
        mScaleY = y
        return this
    }

    fun setShape(type: Int):JJOutlineProvider{
        mIsPath = false
        mIsPathClosure = false
        mShape = type
        return this
    }

    fun setOffset(x: Float,y: Float,percent:Boolean = false):JJOutlineProvider{
        mOffsetX = x
        mOffsetY = y
        mIsOffsetPercent = percent
        return this
    }

    fun setOffset(percentX: Float,percentY: Float,plusX:Float,plusY:Float):JJOutlineProvider{
        mOffsetX = percentX
        mOffsetY = percentY
        mIsOffsetPercent = true
        mOffsetPlusX  = plusX
        mOffsetPlusY = plusY
        return this
    }



    fun setAlpha(@FloatRange(from = 0.0, to = 1.0)alpha: Float): JJOutlineProvider{
        mAlpha = if(alpha == 1f) 0.99f else alpha
        return this
    }

    fun setRadius(radius: Float): JJOutlineProvider{
        mRadius = floatArrayOf(radius,radius,radius,radius,radius,radius,radius,radius)
        mIsPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setRadius(radius: FloatArray) : JJOutlineProvider{
        mRadius = radius
        mIsPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setRadius(topLeft: Float,topRight:Float, bottomRight:Float,bottomLeft:Float) : JJOutlineProvider{
        mRadius = floatArrayOf(topLeft,topLeft,topRight,topRight,bottomRight,bottomRight,bottomLeft,bottomLeft)
        mIsPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun setPath(path:Path):JJOutlineProvider{
        mIsPath = true
        mIsPathClosure = false
        mShape = 0
        mPath = path
        return this
    }

    fun setPath(closure:(RectF,Path)->Unit):JJOutlineProvider{
        mIsPath = true
        mIsPathClosure = true
        mSetupPath = closure
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
    fun setSvgPath(d:String,vbBox:FloatArray,meetOrSlice:Int,align:String,density:Float): JJOutlineProvider {
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


    fun diposePath():JJOutlineProvider{
        mIsPath = false
        mSetupPath = null
        mIsPathClosure = false
        mIsSvgPath = false
        mPath.reset()
        return this
    }


    fun setPadding(padding: JJPadding): JJOutlineProvider {
        mPadding = padding
        return this
    }


    override fun getOutline(view: View?, outline: Outline?) {
        val h = view?.height ?: 0
        val w = view?.width ?: 0
        mRect.set(0f,0f,w.toFloat(),h.toFloat())
        outline?.alpha = mAlpha

        if(mRect.width() > 0f && mRect.height()> 0f) {

            setupRect()

            val isClipToOutline = view?.clipToOutline ?: false
            if(isClipToOutline){
                setupRadiusForShape()
                outline?.setRoundRect(mRect.toRect(),mRadius.first())
            }else {
                //just elevation

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

                if (!mIsPath){
                    mPath.reset()
                    setupRadiusForShape()
                    mPath.addRoundRect(mRect, mRadius, Path.Direction.CW)
                }

                outline?.setConvexPath(mPath)
            }
        }

    }

  private var mMatrix = Matrix()
    private fun setupRect(){
        mMatrix.reset()

        if(mIsOffsetPercent){
            mOffsetX = if(mOffsetX < 0f) 0f else if (mOffsetX > 1f) 1f else mOffsetX
            mOffsetY = if(mOffsetY < 0f) 0f else if (mOffsetY > 1f) 1f else mOffsetY
            val ox = mOffsetX * mRect.width()
            val oy = mOffsetY * mRect.height()
            mRect.offset(ox + mOffsetPlusX, oy + mOffsetPlusY)
        }else{
            mRect.offset(mOffsetX, mOffsetY)
        }
        mRect.padding(mPadding)
        mRect.scale(mScaleX, mScaleY,mMatrix)
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
                val radius = min(mRect.height(),mRect.width()) * 0.03f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            3 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.05f
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
                val radius = min(mRect.height(),mRect.width()) * 0.15f
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


            else -> Log.v("JJOutlineProvider","Custom Radius")
        }
    }
}


