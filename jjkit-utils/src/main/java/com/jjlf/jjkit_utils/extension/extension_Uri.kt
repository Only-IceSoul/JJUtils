package com.jjlf.jjkit_utils.extension

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getFileName(context: Context): String?
        = when (scheme) {
    ContentResolver.SCHEME_FILE -> lastPathSegment
    ContentResolver.SCHEME_CONTENT -> getCursorContent(context)
    else -> null
}

private fun Uri.getCursorContent(context: Context): String?
        = try {
    context.contentResolver.query(this, null, null, null, null)?.let { cursor ->
        cursor.run {
            if (moveToFirst()) getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
            else null
        }.also { cursor.close() }
    }
} catch (e : Exception) { null }