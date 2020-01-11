package com.jjlf.jjkit_utils.extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.compareDateOnly(equalTo: Date): Int{
    val cal = Calendar.getInstance()
    cal.time = this
    val cal2 = Calendar.getInstance()
    cal2.time = equalTo

    return when {
        cal.get(Calendar.YEAR)> cal2.get(Calendar.YEAR) -> 1
        cal.get(Calendar.YEAR)< cal2.get(Calendar.YEAR) -> -1
        else -> {
            when {
                cal.get(Calendar.DAY_OF_YEAR)> cal2.get(Calendar.DAY_OF_YEAR) -> 1
                cal.get(Calendar.DAY_OF_YEAR)< cal2.get(Calendar.DAY_OF_YEAR) -> -1
                else -> 0
            }
        }
    }
}


fun Date.setTimeWithString(defaultFormat:String) {
    time = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH).parse(defaultFormat)?.time ?: return
}

fun Date.setTimeWithString(defaultFormat:String,locale: Locale) {
    time = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy",locale).parse(defaultFormat)?.time ?: return
}

fun Date.setTimeWithString(defaultFormat:String,pattern:String,locale: Locale) {
    time = SimpleDateFormat(pattern,locale).parse(defaultFormat)?.time ?: return
}
