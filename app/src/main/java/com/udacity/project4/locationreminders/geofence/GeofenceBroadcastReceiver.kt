package com.udacity.project4.locationreminders.geofence

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.utils.sendNotification
import timber.log.Timber

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        GeofenceTransitionsJobIntentService.enqueueWork(context,intent)

////TODO: implement the onReceive method to receive the geofencing events at the background
//        val geofencingEvent : GeofencingEvent = GeofencingEvent.fromIntent(intent)!!
//        if(geofencingEvent.hasError()){
//            Timber.e("error ${geofencingEvent.errorCode}")
//            return
//        }
////        geofencingEvent.triggeringGeofences?.clear()
//        val geofenceList : MutableList<Geofence>? = geofencingEvent.triggeringGeofences
//    for(geofence in geofenceList!!){
//      Timber.e ( geofence.requestId)
//    }
//        val location = geofencingEvent.triggeringLocation
//
//        val transition = geofencingEvent.geofenceTransition
//        when(transition){
//            Geofence.GEOFENCE_TRANSITION_ENTER ->{
//                // Creating and sending notification
//                val notificationManager = ContextCompat.getSystemService(
//                    context!!, NotificationManager::class.java
//                ) as NotificationManager
//
////                notificationManager.sendNotification(context,"entered")
//            }
//            Geofence.GEOFENCE_TRANSITION_EXIT ->{
//
//            }
//        }



    }
}