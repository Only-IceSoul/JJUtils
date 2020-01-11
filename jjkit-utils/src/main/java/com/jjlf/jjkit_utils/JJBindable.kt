package com.jjlf.jjkit_utils

class JJBindable<T>( private var data: T? = null) {

    var observer : ((value:T?) -> Unit)? = null

   fun setData(value: T?){
       data = value
       observer?.invoke(data)
   }

   fun getData(): T?{
       return data
   }

}