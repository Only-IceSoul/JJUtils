package com.jjlf.jjkit_utils.inputFilters

import android.text.InputFilter
import android.text.Spanned

class JJInputFilterCounter(maxLength :Int,private val callbackCounter:(count:Int)-> Unit) : InputFilter {

    private var mCounter = 0
    private var mMax = maxLength
    override fun filter(cs: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        var entry : String?
        if (cs == "") { // for backspace
                dest.forEachIndexed { index, d ->
                    val cd = d.toString()
                    if(cd != "\n" && index >= dstart && index < dend) mCounter -= 1
                }
            entry  = null
        }else{
            entry = ""
            mCounter += dstart-dend
            cs.forEach {
                val c = it.toString()
                mCounter += if(c != "\n" && c != "") 1 else 0
                if(mCounter > mMax) mCounter = mMax else if(mCounter == mMax && c == "\n") entry += ""  else entry += c
            }
        }
        callbackCounter.invoke(mCounter)
        return entry
    }
}