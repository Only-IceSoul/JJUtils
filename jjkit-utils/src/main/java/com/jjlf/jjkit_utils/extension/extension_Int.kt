package com.jjlf.jjkit_utils.extension


fun  Int.round(to:  Int):  Int {
    return if (this % to >= to/2)  this + to - this % to else this - this % to

}




