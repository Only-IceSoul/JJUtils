package com.jjlf.jjkit_utils

import java.util.*
import kotlin.random.Random

object JJUtil {

    fun generateUID(extraSize: Int):String{
        val   ds = Date().time.toString()
        val letters = "AJCDENGHIKLVMOPQRSTUWXYZ1234567890"
        var str = ""
        for(element in ds) {
            str += letters[Integer.parseInt(element.toString())]
        }
        for(i in 0 until extraSize) {
            str += letters[Random.nextInt(0,(letters.length - 1))]
        }

        var count = 1
        var newStr = ""
        for(i in str.indices) {
            if(count%2 == 0){
                newStr += str[i].toLowerCase()
            }else{
                newStr += str[i]
            }
            count++
        }

        return newStr;

    }


}