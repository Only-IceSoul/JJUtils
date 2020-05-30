package com.jjlf.jjkit_utils.extension

import java.text.DecimalFormat

fun Double.toFixed(num: Int) : String{
    var pattern = "#."
    if(num > 0 ){
        for(i in 0 until num){
            pattern += "#"
        }
    }else pattern = "#.#"

    val decimalFormat =  DecimalFormat(pattern)
    return decimalFormat.format(this)
}