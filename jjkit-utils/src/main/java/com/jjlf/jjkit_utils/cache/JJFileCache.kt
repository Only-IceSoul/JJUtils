package com.jjlf.jjkit_utils.cache

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import android.graphics.BitmapFactory
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import com.jjlf.jjkit_utils.extension.deleteAsDirectory
import java.io.FileInputStream
import java.lang.IllegalStateException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class JJFileCache{


    companion object{
        const val NOT_DELETED_BUT_EMPTY: Int = 1
        const val DELETED: Int = 0
        const val NOT_DELETED: Int = 2
        const val NOT_COMPLETE_DELETED_SOME_CHILD_ALIVE = 3
    }

    private var mLimitFreeSpaceDisk =  200L  //Mib
    private var mDirectory : File? = null
    private var mQuality = 90
    private var mBitmapConfig =  Bitmap.Config.ARGB_8888
    private var mCompressFormat = Bitmap.CompressFormat.JPEG
    private var mMinHeight = 8
    private var mMinWidth = 8
    private var mPrioritySizeSampling = false
    private var mHalfSpaceDone = false
    private val mLocker = ReentrantLock()

    //meBiBytes 1 Mib = 1048576L bytes
    fun setLimitFreeSpaceDisk(meBiBytes:Long): JJFileCache {
        mLimitFreeSpaceDisk = if(meBiBytes < 1) 1 else meBiBytes
        return this
    }

    fun setDirectory(file:File): JJFileCache {
        mDirectory = file
        return this
    }

    fun setQuality(@IntRange(from = 1, to = 100) int:Int) : JJFileCache {
        mQuality = int
        return this
    }

    fun setBitmapConfig(config:Bitmap.Config): JJFileCache {
        mBitmapConfig = config
        return this
    }

    fun setBitmapCompressFormat(format : Bitmap.CompressFormat) : JJFileCache {
        mCompressFormat = format
        return this
    }

    fun setMinSizeSamplingOOM(reqWidth: Int,reqHeight: Int,priority : Boolean = false){
        mMinHeight = if(reqHeight < 1) 1 else reqHeight
        mMinWidth = if(reqWidth < 1) 1 else reqWidth
        mPrioritySizeSampling = priority
    }

     fun addBitmap(key:String, bitmap:Bitmap?) : Boolean{
         synchronized(mLocker){
             return try {
                 if(key.isEmpty() || bitmap == null) return false
                 if(mDirectory == null){
                     Log.e("JJFileCache","Directory is null")
                     return false
                 }
                 if(!mDirectory!!.exists()) mDirectory?.mkdir()
                 val file =  File(mDirectory!!,"${key.trim()}.jpg")
                 if(file.exists()){
                     Log.e("JJFileCache","File Exists with key $key")
                     return false
                 }

                 if( (mDirectory!!.usableSpace / 1048576L)  < (mLimitFreeSpaceDisk)){
                     if(mDirectory?.deleteAsDirectory(true) == NOT_COMPLETE_DELETED_SOME_CHILD_ALIVE && !mHalfSpaceDone){
                         mHalfSpaceDone = true
                         if(addBitmap(key,bitmap)) {
                             mHalfSpaceDone = false
                             return true
                         }else {
                             val value = mDirectory?.deleteAsDirectory()
                             if (value == DELETED || value == NOT_DELETED_BUT_EMPTY) return writeData(file,bitmap)
                         }
                     }
                     Log.e("JJFileCache","FreeSpace is less than limitFreeSpace")
                     return false
                 }

                 writeData(file,bitmap)
             } catch (e: Exception) {
                 Log.e("JJFileCache","Error:addBitmapToCache $e")
                 false
             }
         }
    }


    private fun writeData(file:File,bitmap:Bitmap) : Boolean{
        val out = FileOutputStream(file)
        val done = bitmap.compress(mCompressFormat, mQuality, out)
        out.flush()
        out.close()
       return done
    }

    fun getBitmap(key: String):Bitmap?{
        mLocker.withLock {
            return try {
                if(mDirectory != null) {
                    val file = File(mDirectory!!, "${key.trim()}.jpg")
                    if(!file.exists()){
                        Log.e("JJFileCache","File not exist")
                        return null
                    }
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = mBitmapConfig
                    BitmapFactory.decodeStream(FileInputStream(file), null, options)
                }else{
                    Log.e("JJFileCache","Directory is null")
                    null
                }
            }catch (e: Exception){
                Log.e("JJFileCache","ERROR:getBitmap  $e")
                null
            }
        }
    }

    fun getBitmapSamplingOOM(context: Context,key:String): Bitmap?{
        mLocker.withLock {
            return try {
                if (mDirectory != null) {
                    val file = File(mDirectory!!, "${key.trim()}.jpg")
                    if (!file.exists()) {
                        Log.e("JJFileCache", "File not exist")
                        return null
                    }
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inPreferredConfig = mBitmapConfig
                    BitmapFactory.decodeStream(FileInputStream(file), null, options)
                    options.inSampleSize = calculateInSampleSizeOOM(context, options)
                    options.inJustDecodeBounds = false
                    BitmapFactory.decodeStream(FileInputStream(file), null, options)
                } else {
                    Log.e("JJFileCache", "Directory is null")
                    null
                }
            } catch (e: Exception) {
                Log.e("JJFileCache", "ERROR:getBitmapSampling: $e")
                null
            }
        }
    }

    private fun calculateInSampleSizeOOM(context: Context,options: BitmapFactory.Options): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        val memInfo = getNativeMemoryInfo(context)
        val memFreeSpace = if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)  memInfo.availMem - (memInfo.threshold * 2)
        else getHeapFreeSpace()

        if(height > 0 && width > 0) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            while (getBitmapSize(height/inSampleSize,width/inSampleSize) >= memFreeSpace && isValidSize(width,height,inSampleSize) ) {
                inSampleSize *= 2
            }
            if(getBitmapSize(height/inSampleSize,width/inSampleSize) >= memFreeSpace)  throw OutOfMemoryError("No space for get image OOM")
            if(!isValidSize(width,height,inSampleSize) && mPrioritySizeSampling) throw IllegalStateException("min Size sampling overpassed")
            if(isSizeZero(width,height,inSampleSize)) throw IllegalStateException("failed sampling size is zero")
        }

        return inSampleSize
    }

    private fun isSizeZero(w:Int, h:Int, inSampleSize: Int): Boolean{
        return h/inSampleSize < 1 && w/inSampleSize < 1
    }

    private fun isValidSize(w:Int, h:Int, inSampleSize: Int) : Boolean{
        return  h/inSampleSize >= mMinHeight && w/inSampleSize >= mMinWidth
    }

    private fun getBitmapSize(h:Int, w:Int):Long{
        return h * w * 4L
    }


    private fun getNativeMemoryInfo(context: Context) : ActivityManager.MemoryInfo{
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo
    }

    private fun getHeapFreeSpace(): Long{
        val runtime = Runtime.getRuntime()
        return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory())
    }

}