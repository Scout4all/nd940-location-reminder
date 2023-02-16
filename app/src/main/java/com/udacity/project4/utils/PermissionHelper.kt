package com.udacity.project4.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionHelper() {
    lateinit var context:Activity
    lateinit var permission : String
    var requestCode :Int = 0

    fun setData(context:Activity,
                      permission : String,
                      requestCode :Int){
        this.context = context
        this.permission = permission
        this.requestCode = requestCode

    }
   @Suppress("DEPRECATED_IDENTITY_EQUALS")
   private fun checkPermission(): Boolean{
       return ContextCompat.checkSelfPermission(
           context,
           permission
       ) === PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() : Boolean{
         if(!checkPermission()){
             ActivityCompat.requestPermissions(
                 context,
                 arrayOf(permission),
                 requestCode
             )
             return false
         }else{
             return true
         }
    }
}