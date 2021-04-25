package com.jjlf.jjkit_utils.recylcerView

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
class JJRecyclerScrollListener : RecyclerView.OnScrollListener(){

    private var mOnEndReached: (()->Unit)? = null
    fun setEndReached(cb:()->Unit){
        mOnEndReached = cb
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)


        handleEndReached(recyclerView,newState)
    }

    private fun handleEndReached(recyclerView: RecyclerView,newState: Int){
        recyclerView.adapter?.let { ad ->
            if(recyclerView.layoutManager is GridLayoutManager || recyclerView.layoutManager is LinearLayoutManager ){
                (recyclerView.layoutManager as? LinearLayoutManager)?.let {lm ->
                    if(lm.findLastVisibleItemPosition() == ad.itemCount - 1 && newState == RecyclerView.SCROLL_STATE_IDLE){
                        mOnEndReached?.invoke()
                    }
                }
            }
        }
    }
}
