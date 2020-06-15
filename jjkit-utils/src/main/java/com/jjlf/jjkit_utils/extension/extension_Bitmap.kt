package com.jjlf.jjkit_utils.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.math.max
import kotlin.math.min

fun Bitmap.sizeBytesInMap() : Long {
    if(android.os.Build.VERSION.SDK_INT >= 19){
        return byteCount + 200L
    }
    return (rowBytes * height) + 200L
}


@Suppress("DEPRECATION")
fun Bitmap.getBytesPerPixel(): Int = when (config) {
        Bitmap.Config.ARGB_8888 -> 4
        Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
        Bitmap.Config.ALPHA_8 -> 1
        else -> 1
    }

@RequiresApi(android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
fun Bitmap.effectBlur(context: Context,radius: Float) : Bitmap? {
    var rs : RenderScript? = null
    var  input : Allocation? = null
    var output : Allocation? = null
    var blur : ScriptIntrinsicBlur? = null
    try {
        rs = RenderScript.create(context)
        rs.messageHandler =  RenderScript.RSMessageHandler()
        input = Allocation.createFromBitmap(rs, this, Allocation.MipmapControl.MIPMAP_NONE,
            Allocation.USAGE_SCRIPT)
        output = Allocation.createTyped(rs, input.type)
        blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blur.setInput(input)
        blur.setRadius(radius)
        blur.forEach(output)
        output.copyTo(this)
    } catch (e: Exception){
        Log.e("JJKit","Bitmap : effectBlur  error $e")
    } finally {
        rs?.destroy()
        input?.destroy()
        output?.destroy()
        blur?.destroy()
    }

    return this
}


fun Bitmap.effectStackBlur(radius: Int, canReuseInBitmap: Boolean = true): Bitmap? {

    val bitmapResult: Bitmap = if (canReuseInBitmap) {
        this
    } else {
        this.copy(this.config, true)
    }
    if (radius < 1) {
        return null
    }
    val w = bitmapResult.width
    val h = bitmapResult.height
    val pix = IntArray(w * h)
    bitmapResult.getPixels(pix, 0, w, 0, 0, w, h)
    val wm = w - 1
    val hm = h - 1
    val wh = w * h
    val div = radius + radius + 1
    val r = IntArray(wh)
    val g = IntArray(wh)
    val b = IntArray(wh)
    var rsum: Int
    var gsum: Int
    var bsum: Int
    var x: Int
    var y: Int
    var i: Int
    var p: Int
    var yp: Int
    var yi: Int
    var yw: Int
    val vmin = IntArray(max(w, h))
    var divsum = div + 1 shr 1
    divsum *= divsum
    val dv = IntArray(256 * divsum)
    i = 0
    while (i < 256 * divsum) {
        dv[i] = i / divsum
        i++
    }
    yi = 0
    yw = yi
    val stack =
        Array(div) { IntArray(3) }
    var stackpointer: Int
    var stackstart: Int
    var sir: IntArray
    var rbs: Int
    val r1 = radius + 1
    var routsum: Int
    var goutsum: Int
    var boutsum: Int
    var rinsum: Int
    var ginsum: Int
    var binsum: Int
    y = 0
    while (y < h) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        i = -radius
        while (i <= radius) {
            p = pix[yi + Math.min(wm, Math.max(i, 0))]
            sir = stack[i + radius]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rbs = r1 - Math.abs(i)
            rsum += sir[0] * rbs
            gsum += sir[1] * rbs
            bsum += sir[2] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            i++
        }
        stackpointer = radius
        x = 0
        while (x < w) {
            r[yi] = dv[rsum]
            g[yi] = dv[gsum]
            b[yi] = dv[bsum]
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radius + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (y == 0) {
                vmin[x] = Math.min(x + radius + 1, wm)
            }
            p = pix[yw + vmin[x]]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]
            rsum += rinsum
            gsum += ginsum
            bsum += binsum
            stackpointer = (stackpointer + 1) % div
            sir = stack[stackpointer % div]
            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]
            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]
            yi++
            x++
        }
        yw += w
        y++
    }
    x = 0
    while (x < w) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        yp = -radius * w
        i = -radius
        while (i <= radius) {
            yi = Math.max(0, yp) + x
            sir = stack[i + radius]
            sir[0] = r[yi]
            sir[1] = g[yi]
            sir[2] = b[yi]
            rbs = r1 - Math.abs(i)
            rsum += r[yi] * rbs
            gsum += g[yi] * rbs
            bsum += b[yi] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            if (i < hm) {
                yp += w
            }
            i++
        }
        yi = x
        stackpointer = radius
        y = 0
        while (y < h) {
            // Preserve alpha channel: ( 0xff000000 & pix[yi] )
            pix[yi] =
                -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radius + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (x == 0) {
                vmin[y] = Math.min(y + r1, hm) * w
            }
            p = x + vmin[y]
            sir[0] = r[p]
            sir[1] = g[p]
            sir[2] = b[p]
            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]
            rsum += rinsum
            gsum += ginsum
            bsum += binsum
            stackpointer = (stackpointer + 1) % div
            sir = stack[stackpointer]
            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]
            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]
            yi += w
            y++
        }
        x++
    }
    bitmapResult.setPixels(pix, 0, w, 0, 0, w, h)

    return bitmapResult
}


fun Bitmap.toBitmapThumbnail() : Bitmap?{
   return try {
        val bytesCache = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 25, bytesCache)
        val bitmapQuality = BitmapFactory.decodeStream(ByteArrayInputStream(bytesCache.toByteArray()))
        val matrix = Matrix()
        val scale = min(100 / bitmapQuality.width.toFloat(), 100 / bitmapQuality.height.toFloat())
        matrix.postScale(scale, scale)
        val bmp = Bitmap.createBitmap(bitmapQuality, 0, 0, bitmapQuality.width, bitmapQuality.height, matrix, true)
        bitmapQuality.recycle()
        bmp
    }catch (e:Exception){
       Log.e("JJKit"," Bitmap:toBitmapThumbnail $e ")
        null
    }
}



fun Bitmap.RGB565toARGB888() : Bitmap {
    val numPixels = this.width * this.height
    val pixels =  IntArray(numPixels)

    //Get JPEG pixels.  Each int is the color values for one pixel.
    this.getPixels(pixels, 0, this.width, 0, 0, this.width, this.height)

    //Create a Bitmap of the appropriate format.
    val result = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

    //Set RGB pixels.
    result.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
    return result
}

fun Bitmap.toByteArrayJPEG(quality:Int) : ByteArray?{
    val baos = ByteArrayOutputStream()
    return if( compress(Bitmap.CompressFormat.JPEG, quality, baos)) baos.toByteArray() else null
}

fun Bitmap.toByteArrayPNG() : ByteArray?{
    val baos = ByteArrayOutputStream()
    return if(compress(Bitmap.CompressFormat.PNG, 100, baos)) baos.toByteArray() else null
}
