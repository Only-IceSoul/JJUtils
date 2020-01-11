package com.jjlf.jjkit_utils.cache


import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.util.LruCache
import java.lang.Exception


open class JJBitmapCache {

    private var mHeapMemoryPercentLimit = 80
    private val mLruCache : LruCache<String,Bitmap>
    private val mCacheSize = 300 * 1024

    init {
        mLruCache = object : LruCache<String,Bitmap>(mCacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    fun put(context: Context?, key:String?, bitmap: Bitmap?){
        if(key == null || key.isEmpty() || context == null || bitmap == null) return
          synchronized(this) {
              try {
                  var divisor = 2
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                      while (!isAvailableFreeSpaceNativeMemory(context,bitmap)) {
                          if(mLruCache.size() < 1024) throw OutOfMemoryError("No space for add bitmap and cache cleaned to ${mLruCache.size()} Kib")
                          mLruCache.trimToSize( mCacheSize/divisor)
                          divisor *= 2
                      }
                  } else {
                      while (getFuturePercentHeapMemoryUsage(bitmap) > mHeapMemoryPercentLimit) {
                          if(mLruCache.size() < 1024) throw OutOfMemoryError("No space for add bitmap and cache cleaned to ${mLruCache.size()} Kib")
                          mLruCache.trimToSize( mCacheSize/divisor)
                          divisor *= 2
                      }
                  }
                    mLruCache.put(key, bitmap)
              }catch (e: Exception){
                  Log.e("JJBitmapCache","Error:put: $e")
              }

          }
    }

    fun get(key: String?): Bitmap?{
        if(key == null || key.isEmpty()) return null
        return mLruCache.get(key)
    }

    fun remove(key:String?){
        if(key == null || key.isEmpty()) return
        mLruCache.remove(key)
    }

    private fun isAvailableFreeSpaceNativeMemory(context:Context,bitmap: Bitmap): Boolean{
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        val limitFreeSpace = memoryInfo.threshold * 2
        return memoryInfo.availMem  > limitFreeSpace + bitmap.byteCount
    }

    private fun getFuturePercentHeapMemoryUsage(bitmap: Bitmap) : Long {
        val runtime  = Runtime.getRuntime()
        val memUsage = runtime.totalMemory() - runtime.freeMemory()
        val futureSize = memUsage + bitmap.byteCount
        return ((futureSize * 100) / runtime.maxMemory())
    }





}