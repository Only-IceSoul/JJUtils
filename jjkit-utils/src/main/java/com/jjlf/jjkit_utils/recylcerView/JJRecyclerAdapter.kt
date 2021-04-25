package com.jjlf.jjkit_utils.recylcerView

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class JJRecyclerAdapter<T>(private val mItems: MutableList<T?> = mutableListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){



    private var mOnCreateViewHolder : ((parent: ViewGroup, viewType: Int)-> RecyclerView.ViewHolder)? = null
    private var mOnBindViewHolder: ((holder: RecyclerView.ViewHolder,item:T?, position: Int)->Unit)? = null
    private var mOnItemViewType: ((item:T?, position:Int) -> Int)? = null


    override fun getItemViewType(position: Int): Int {
        return mOnItemViewType?.invoke(mItems[position],position) ?: 0
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return mOnCreateViewHolder!!.invoke(parent,viewType)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mOnBindViewHolder?.invoke(holder,mItems[position],position)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun setOnCreateViewHolder(creator:(parent: ViewGroup, viewType: Int)-> RecyclerView.ViewHolder): JJRecyclerAdapter<T> {
         mOnCreateViewHolder = creator
         return this
    }

    fun setOnBindViewHolder(bind: (holder: RecyclerView.ViewHolder,item:T?, position: Int)->Unit): JJRecyclerAdapter<T> {
        mOnBindViewHolder =  bind
        return this
    }


    fun newData(list: List<T?>){
        mItems.clear()
        mItems.addAll(list)
        notifyDataSetChanged()
    }

    fun getListFilteredNotRepeatAsync(list: List<T?>, result: (MutableList<T?>)->Unit){
       Thread{
        val finalList =    list.filter {
               it != null && !mItems.contains(it)
           } as MutableList<T?>
           result.invoke(finalList)
           Thread.currentThread().interrupt()
       }.start()
    }

    fun add(list: List<T?>){
            if(list.isNotEmpty()) {
                val posStart = mItems.size
                mItems.addAll(list)
                notifyItemRangeInserted(posStart, list.size)
            }

    }

    fun add(obj: T){
            mItems.add(obj)
            notifyItemInserted(mItems.size)
    }

    fun addToIndex(index: Int, obj: T){
            mItems.add(index,obj)
            notifyItemInserted(index)
    }

    fun updateToIndex(index:Int,obj: T){
            mItems[index] = obj
            notifyItemChanged(index)
    }

    fun removeAt(index: Int){
        mItems.removeAt(index)
        notifyItemRemoved(index)
    }

    //u need override equals for a better search
    fun findRemovedAsync(context: Activity, obj: T){
            val tr = Thread {
                try {
                    var foundIndex = -1
                    mItems.forEachIndexed { index, t ->
                        if (t != null) {
                            if (t == obj) {
                                foundIndex = index
                                return@forEachIndexed
                            }
                        }
                    }
                    if (foundIndex >= 0) {
                        mItems.removeAt(foundIndex)
                        context.runOnUiThread {
                            notifyItemRemoved(foundIndex)
                        }
                    }else{
                        Log.e("JJKit", "JJRecyclerAdapter:findRemovedAsync item not found")
                    }
                } catch (e: InterruptedException) {
                    Log.e("JJKit", "JJRecyclerAdapter:findRemovedAsync $e")
                } finally {
                    Thread.currentThread().interrupt()
                }
            }
            tr.priority = 4
            tr.start()
    }

    //u need override equals for a better search or wrong item can be eliminated sample: id == id
    fun findUpdateModifiedAsync(context: Activity, obj: T){
           val  tr = Thread{
                try {
                    var foundIndex = -1
                    mItems.forEachIndexed { index, t ->
                        if (t != null) {
                            if (t == obj) {
                                foundIndex = index
                                return@forEachIndexed
                            }
                        }
                    }
                    if (foundIndex >= 0) {
                        mItems[foundIndex] = obj
                        context.runOnUiThread {
                            notifyItemChanged(foundIndex)
                        }
                    }
                }catch (e:InterruptedException){
                    Log.e("JJKit","JJRecyclerAdapter:findUpdateModifiedAsync $e")
                }finally {
                    Thread.currentThread().interrupt()
                }
            }
            tr.priority = 4
            tr.start()
    }

    fun removeLastIfNull(){
        val position = mItems.size - 1
        if(position >= 0) {
            if (mItems[position] == null) {
                mItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }


    fun getList(): MutableList<T?>{
        return mItems
    }


}