package com.jjlf.jjkit_utils.extension

import android.os.Build
import android.os.StatFs
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

val File.NOT_DELETED_BUT_EMPTY: Int
    get() = 1
val File.DELETED: Int
    get() = 0
val File.NOT_DELETED: Int
    get() = 2
val File.NOT_COMPLETE_DELETED_SOME_CHILD_ALIVE
    get() = 3

fun File.deleteAsChild(): Boolean{
    if(!this.exists() || this.isDirectory) return true
    return this.delete()
}

fun File.deleteAsDirectory(deleteHalf: Boolean = false): Int{
    if(!this.exists()) return DELETED
    var success = true
    if (this.isDirectory) {
        val files = this.listFiles()
        if (files != null) {
            if(files.isNotEmpty()){
                files.forEachIndexed { index, it ->
                    if(deleteHalf && index == ((files.size/2) - 1)) return NOT_COMPLETE_DELETED_SOME_CHILD_ALIVE
                    success = if (it.isDirectory) {
                        val value = it.deleteAsDirectory()
                        success and (value == NOT_DELETED_BUT_EMPTY || value == DELETED)
                    }else {
                        success and it.deleteAsChild()
                    }
                }
            }else {
                return if (!this.delete()) {
                    Log.e("deleteFileDirectory", "Failed to delete directory but is empty")
                    NOT_DELETED_BUT_EMPTY
                }else DELETED
            }
        } else {
            return if (!this.delete()) {
                Log.e("deleteFileDirectory", "Failed to delete directory")
                NOT_DELETED
            }else DELETED
        }
    } else {
        return DELETED
    }
    return if(success) DELETED else NOT_COMPLETE_DELETED_SOME_CHILD_ALIVE

}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun File.statFsAviableSpaceBytes():Long{
    val stat = StatFs(path)
    val blockSize = stat.blockSizeLong
    val availableBlocks = stat.availableBlocksLong
    return blockSize*availableBlocks
}


