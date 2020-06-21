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



fun Date.toCalendar(): Calendar{
    val c = Calendar.getInstance()
    c.time = this
    return  c
}

fun Date.toStringLocal(sep:String="/",sep2:String="", hour:Boolean=true, min:Boolean=true, sec:Boolean= true,format24:Boolean = true): String {
    val c = Calendar.getInstance()
    c.time = this
    val year =  getYear2digit(c)
    val day = getDay2digit(c)
    val month = getMonth2digit(c)
    val h = getHour2digit(c,format24)
    val m = getMin2digit(c)
    val s = getSec2digit(c)
    val result = StringBuilder("$day$sep$month$sep$year$sep2")
    if(hour) result.append(h)
    if(min) {
        result.append(":")
        result.append(m)
    }
    if(sec) {
        result.append(":")
        result.append(s)
    }
    return  result.toString()
}

fun Date.getHour2digit(c:Calendar,format24:Boolean = true):String{
    val h = if(format24) c.get(Calendar.HOUR_OF_DAY) else c.get(Calendar.HOUR) + 1
    val s = h.toString()
    val count = s.count()
    return if(count > 1) s else "0$s"
}

fun Date.getMin2digit(c:Calendar):String{
    val v = c.get(Calendar.MINUTE).toString()
    val count = v.count()
    return if(count > 1) v else "0$v"
}

fun Date.getSec2digit(c:Calendar):String{
    val v = c.get(Calendar.SECOND).toString()
    val count = v.count()
    return if(count > 1) v else "0$v"
}


fun Date.getHour2digit(format24:Boolean = true):String{
    val c = Calendar.getInstance()
    c.time = this
    val h = if(format24) c.get(Calendar.HOUR_OF_DAY) else c.get(Calendar.HOUR) + 1
    val s = h.toString()
    val count = s.count()
    return if(count > 1) s else "0$s"
}

fun Date.getMin2digit():String{
    val c = Calendar.getInstance()
    c.time = this
    val v = c.get(Calendar.MINUTE).toString()
    val count = v.count()
    return if(count > 1) v else "0$v"
}

fun Date.getSec2digit():String{
    val c = Calendar.getInstance()
    c.time = this
    val v = c.get(Calendar.SECOND).toString()
    val count = v.count()
    return if(count > 1) v else "0$v"
}


fun Date.getYear2digit(c:Calendar):String {
    val y =  c.get(Calendar.YEAR).toString()
    val count = y.count()
    return if(count > 1) "${y[count-2]}${y[count-1]}" else y
}

fun Date.getDay2digit(c:Calendar):String {
    val y =  c.get(Calendar.DAY_OF_MONTH).toString()
    val count = y.count()
    return if(count > 1) y else "0$y"
}

fun Date.getMonth2digit(c:Calendar):String {
    val y =  (c.get(Calendar.MONTH) + 1).toString()
    val count = y.count()
    return if(count > 1) y else "0$y"
}

fun Date.getYear2digit():String {
    val c = Calendar.getInstance()
    c.time = this
    val y =  c.get(Calendar.YEAR).toString()
    val count = y.count()
    return if(count > 1) "${y[count-2]}${y[count-1]}" else y
}

fun Date.getDay2digit():String {
    val c = Calendar.getInstance()
    c.time = this
    val y =  c.get(Calendar.DAY_OF_MONTH).toString()
    val count = y.count()
    return if(count > 1) y else "0$y"
}

fun Date.getMonth2digit():String {
    val c = Calendar.getInstance()
    c.time = this
    val y =  (c.get(Calendar.MONTH) + 1).toString()
    val count = y.count()
    return if(count > 1) y else "0$y"
}
