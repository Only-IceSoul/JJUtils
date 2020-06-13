package com.jjlf.jjkit_utils.inputFilters

import android.text.InputFilter
import android.text.Spanned

class JJInputFilterLettersNumber : InputFilter {

    override fun filter(cs: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        if (cs == "") { // for backspace
            return null
        }
        return if (cs.toString().matches("[^a-zA-Z0-9]".toRegex())) {
            ""
        } else null
    }
}