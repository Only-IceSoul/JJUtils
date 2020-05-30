package com.jjlf.jjkit_utils.extension

import android.graphics.Point
import kotlin.math.atan2


fun Point.offset(newPoint: Point) : Point {
    return Point(newPoint.x - x, newPoint.y - y)
}

/**
 *  Calculate angle of a vector.
 *  start right top, end right bottom,  from 0 to 0 (360)
 *
 *  @param target point coordinate x,y vector
 *  @param cw   reverse
 *
 *  @return the measurement of the anglein degrees.
 *
 */
fun Point.rotationAngleTo(target:Point,cw:Boolean = false): Float{
    val theta = atan2((target.y - y).toDouble(), (target.x - x).toDouble())
    var angle =  if(cw) (Math.toDegrees(theta) * -1) + 360 else Math.toDegrees(theta) + 360
    angle = if(angle > 360) angle - 360 else if(angle == 360.0) 0.0 else angle
    return  angle.toFloat()
}