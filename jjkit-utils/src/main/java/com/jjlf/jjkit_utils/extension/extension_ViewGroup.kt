package com.jjlf.jjkit_utils.extension

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

fun ViewGroup.getTextView(id:Int): TextView {
    return findViewById<TextView>(id)
}

fun ViewGroup.getImageView(id:Int): ImageView {
    return findViewById<ImageView>(id)
}

fun ViewGroup.getRawView(id:Int): View {
    return findViewById<View>(id)
}
fun ViewGroup.getButton(id:Int): Button {
    return findViewById<Button>(id)
}

fun <T: View> ViewGroup.getView(id:Int): T {
    return findViewById(id)
}

fun <T: View> ViewGroup.getView(parent:Int, child:Int): T {
    return findViewById<ViewGroup>(parent).findViewById(child) as T
}

fun <T: View> ViewGroup.getView(one:Int, two:Int, three:Int): T {
    return findViewById<ViewGroup>(one).findViewById<ViewGroup>(two).findViewById(three) as T
}



