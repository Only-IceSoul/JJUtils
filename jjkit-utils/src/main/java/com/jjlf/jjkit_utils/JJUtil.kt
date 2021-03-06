package com.jjlf.jjkit_utils

import android.view.View
import java.security.SecureRandom
import java.util.*
import kotlin.random.Random

object JJUtil {

    private val rand =  SecureRandom()

    fun autoId() : String {
        val builder =  StringBuilder()
        val maxRandom = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length
        for(i in 0 until 20) {
            builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"[rand.nextInt(maxRandom)])
        }
        return builder.toString()
    }


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
            newStr += if(count%2 == 0){
                str[i].toLowerCase()
            }else{
                str[i]
            }
            count++
        }

        return newStr

    }


    fun assertEmpty(vararg arr :  String ): Boolean{
        arr.forEach {
            if(it.isEmpty()){
                return false
            }
        }
        return true
    }

    fun enable(vararg arr: View){
        arr.forEach {
            it.isEnabled = true
        }
    }

    fun disable(vararg arr: View){
        arr.forEach {
            it.isEnabled = false
        }
    }


}