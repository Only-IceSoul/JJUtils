package com.jjlf.jjkit_utils.recylcerView

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class JJRecyclerAdapter<T>(private val items: MutableList<T?> = mutableListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mToken = 0

    private var mFetchMoreCallback: (()->Unit)? = null
    private var mViewCreator : ((Context)-> View)? = null
    private var mProgressViewCreator : ((Context)-> View)? = null
    private var mErrorViewCreator : ((Context)-> View)? = null
    private var mBindCallback: ((T?,View,Int)->Unit)? = null

    private var mMemoryPercentLimit = 75
    private var mShowErrorView = false
    private var mIsAddedError = false
    private var mIsErrorEnabled = false

    //if last is null calling fetch more
    override fun getItemViewType(position: Int): Int {
        val value = if(position == items.size - 1 && items.last() == null && mShowErrorView) 2
        else if(position == items.size - 1 && items.last() == null)  1 else 0
        if(value == 1) mFetchMoreCallback?.invoke()
        return value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var holder : RecyclerView.ViewHolder
        when(viewType){
            0 -> {
                val v = mViewCreator!!.invoke(parent.context)
                holder = ViewHolder(v)
            }
            1 -> {
                val view = mProgressViewCreator!!.invoke(parent.context)
                holder = ViewLoad(view)
            }
            2 -> {
                val v = mErrorViewCreator!!.invoke(parent.context)
                holder = ViewError(v)
            }
        }
        return holder

    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder) {
            val c = items[position]
            mBindCallback?.invoke(c,holder.view,position)
        }
    }


    fun setToken(num:Int) : JJRecyclerAdapter<T> {
         mToken = num
        return this
    }

    fun getToker():Int{
        return mToken
    }

    fun setViewCreator(creator:(Context)->View): JJRecyclerAdapter<T> {
         mViewCreator = creator
         return this
    }
    fun setProgressViewCreator(creator:(Context)->View): JJRecyclerAdapter<T> {
        mProgressViewCreator = creator
        return this
    }

    fun setErrorViewCreator(creator:(Context)->View): JJRecyclerAdapter<T> {
        mErrorViewCreator = creator
        return this
    }

    fun setOnBindCallback(bind: (item:T?,View,position:Int)->Unit): JJRecyclerAdapter<T> {
        mBindCallback = bind
        return this
    }

    fun setFetchMoreCallback(callback: ()->Unit) : JJRecyclerAdapter<T> {
        mFetchMoreCallback = callback
        return this
    }


    fun newData(list: List<T?>, token:Int = 0){
        if(isMemoryAvailable() && token == mToken){
                items.clear()
                items.addAll(list)
                notifyDataSetChanged()
        }else{
            if(!mIsAddedError) addError()
        }
    }

    fun getListFilteredNotRepeatAsync(list: List<T?>, result: (MutableList<T?>)->Unit){
       Thread{
        val finalList =    list.filter {
               it != null && !items.contains(it)
           } as MutableList<T?>
           result.invoke(finalList)
           Thread.currentThread().interrupt()
       }.start()
    }

    fun insert(list: List<T?>, token:Int = 0){
        if(isMemoryAvailable() && token == mToken) {
                val posStart = items.size
                items.addAll(list)
                notifyItemRangeInserted(posStart, list.size)
        }else{
            if(!mIsAddedError) addError()
        }
    }

    fun insertToIndex(index: Int, obj: T, token : Int = 0){
        if(isMemoryAvailable() && token == mToken) {
            items.add(index,obj)
            notifyItemInserted(index)
        }
    }

    fun updateToIndex(index:Int,obj: T,token: Int=0){
        if(isMemoryAvailable() && token == mToken) {
            items[index] = obj
            notifyItemChanged(index)
        }
    }

    fun removeAt(index: Int,token: Int = 0){
        if(token == mToken) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    //u need override equals for a better search
    private var mThreadRemove : Thread? = null
    fun findUpdateRemovedAsync(context: Activity, obj: T, token: Int = 0){
        if(isMemoryAvailable() && mToken == token) {
            mThreadRemove = Thread {
                try {
                    var foundIndex = -1
                    items.forEachIndexed { index, t ->
                        if(mThreadRemove?.isInterrupted == true) return@Thread
                        if (t != null) {
                            if (t == obj) {
                                foundIndex = index
                                return@forEachIndexed
                            }
                        }
                    }
                    if (foundIndex >= 0) {
                        if(mThreadRemove?.isInterrupted == true) return@Thread
                        items.removeAt(foundIndex)
                        if(mThreadRemove?.isInterrupted == true) return@Thread
                        context.runOnUiThread {
                            notifyItemRemoved(foundIndex)
                        }
                    }
                } catch (e: InterruptedException) {
                    Log.e("JJKit", "JJRecyclerAdapter:findUpdateRemovedAsync $e")
                } finally {
                    mThreadRemove?.interrupt()
                }
            }
            mThreadRemove?.priority = 4
            mThreadRemove?.start()
        }else{
            if(!mIsAddedError) addError()
        }
    }

    //u need override equals for a better search or wrong item can be eliminated sample: id == id
    private var mThreadModified : Thread? = null
    fun findUpdateModifiedAsync(context: Activity, obj: T, token: Int = 0){
        if(isMemoryAvailable() && mToken == token){
             mThreadModified = Thread{
                try {
                    var foundIndex = -1
                    items.forEachIndexed { index, t ->
                        if(mThreadRemove?.isInterrupted == true) return@Thread
                        if (t != null) {
                            if (t == obj) {
                                foundIndex = index
                                return@forEachIndexed
                            }
                        }
                    }
                    if (foundIndex >= 0) {
                        if(mThreadRemove?.isInterrupted == true) return@Thread
                        items[foundIndex] = obj
                        if(mThreadRemove?.isInterrupted == true) return@Thread
                        context.runOnUiThread {
                            notifyItemChanged(foundIndex)
                        }
                    }
                }catch (e:InterruptedException){
                    Log.e("JJKit","JJRecyclerAdapter:findUpdateModifiedAsync $e")
                }finally {
                    mThreadModified?.interrupt()
                }
            }
            mThreadModified?.priority = 4
            mThreadModified?.start()
        }else{
            if(!mIsAddedError) addError()
        }
    }

    fun removeLastIfNull(token:Int = 0){
        if(token == mToken){
            val position = items.size - 1
            if(items[position] == null){
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun interruptAllThreads(){
          mThreadModified?.interrupt()
        mThreadRemove?.interrupt()

    }

    fun isErrorEnabled(boolean: Boolean) : JJRecyclerAdapter<T>{
        mIsErrorEnabled = boolean
        return this
    }

    fun addError(){
        if(mIsErrorEnabled){
            removeLastIfNull()
            items.add(null)
            mShowErrorView = true
            mIsAddedError = true
            notifyItemInserted(items.size - 1)
        }
    }

    private fun isMemoryAvailable(): Boolean{
        val runtime = Runtime.getRuntime()
        val currentPercent = ((runtime.totalMemory() - runtime.freeMemory()) * 100) / runtime.maxMemory()
        return currentPercent < mMemoryPercentLimit
    }

    fun getList(): MutableList<T?>{
        return items
    }



    class ViewLoad(val view: View) : RecyclerView.ViewHolder(view)

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class ViewError(val view: View) : RecyclerView.ViewHolder(view)

}