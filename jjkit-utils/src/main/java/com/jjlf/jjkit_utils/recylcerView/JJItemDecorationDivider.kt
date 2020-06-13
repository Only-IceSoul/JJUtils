package com.jjlf.jjkit_utils.recylcerView

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.InsetDrawable
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.jjlf.jjkit_layoututils.JJPadding


class JJItemDecorationDivider(context: Context,orientation:Int) : DividerItemDecoration(context,orientation) {

    private val mOri = orientation
    private var mOffset = 0

    init {
        if(mOri == VERTICAL) mOffset = drawable?.intrinsicHeight ?: 0
        if(mOri == HORIZONTAL)  mOffset = drawable?.intrinsicWidth ?: 0
    }

    fun setOffset(size:Int): JJItemDecorationDivider {
        mOffset = size
        return this
    }

    fun setDrawablePadding(pad: JJPadding): JJItemDecorationDivider {
        setDrawable(InsetDrawable(drawable,pad.left,pad.top,pad.right,pad.bottom))
        return this
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if(mOri == VERTICAL) outRect.set(0,0,0,mOffset)
        if(mOri == HORIZONTAL) outRect.set(0,0,mOffset,0)
    }

}