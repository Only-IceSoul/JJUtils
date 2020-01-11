package com.jjlf.jjkit_utils.recylcerView

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jjlf.jjkit_layoututils.JJMargin


class JJItemDecorationMargin(private val offSet: JJMargin) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(offSet.left, offSet.top, offSet.right, offSet.bottom)
    }
}