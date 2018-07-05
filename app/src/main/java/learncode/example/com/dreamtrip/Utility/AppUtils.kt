package com.example.admin2base.myapplication.utils

import android.util.Log

/**
 * Created by admin2base on 19/5/18.
 */
class AppUtils private constructor(){


    var debug_status=true

    companion object {
        private var utils: AppUtils = AppUtils()

        val instance: AppUtils
            get() = utils
    }

    fun debugLog(tag:String, message:String){
        if(debug_status) {
            Log.d(tag, message)
        }
    }

    fun errorLog(tag:String, message:String){
        if(debug_status) {
            Log.e(tag, message)
        }
    }
}