package com.jjlf.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var num = 1
        val result = 2.let { num *= it ;
            Log.e("ICESOUL","FIRst")
            num } * 2.let { num *= it ;
            Log.e("ICESOUL","Second")
            num} * 1

        Log.e("ICESOUL","resul: $result")
    }

}
