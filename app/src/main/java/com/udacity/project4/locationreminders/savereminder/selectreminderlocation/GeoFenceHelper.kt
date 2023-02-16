package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.utils.ACTION_GEOFENCE_EVENT
import timber.log.Timber

class GeoFenceHelper(base: Context?) : ContextWrapper(base) {

      val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(base!!.applicationContext)




     val geofenceIntent: PendingIntent by lazy {
//         val intent = Intent("com.aol.android.geofence.ACTION_RECEIVE_GEOFENCE")
//            Intent intent = new Intent(context, ReceiveTransitionsIntentService.class);

         val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
//         intent.action = ACTION_GEOFENCE_EVENT

         PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun geoFencingRequest(geofence: Geofence) : GeofencingRequest {

     return GeofencingRequest.Builder().addGeofence(geofence)
         .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
         .build()

 }

    fun getGeoFence(id : String, latLng: LatLng,radius:Float,transitionTypes : Int) : Geofence {
   return Geofence.Builder().setCircularRegion(latLng.latitude,latLng.longitude,radius)
        .setRequestId(id)
        .setTransitionTypes(transitionTypes)
       .setLoiteringDelay(5000)
       .setExpirationDuration(Geofence.NEVER_EXPIRE)
       .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
       .build()
    }

    fun removeGeofence(){
    geofencingClient.removeGeofences(geofenceIntent).run {
        addOnFailureListener {
            Timber.d("e ${it.localizedMessage}")
        }
    }
    }



    fun errorString(e : Exception) : String{
       if(e is ApiException  ){
           val apiException = e as ApiException
         return  when(apiException.statusCode){
               GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GEOFENCE_NOT_AVAILABLE"
               GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "GEOFENCE_TOO_MANY_GEOFENCES"
               GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "GEOFENCE_TOO_MANY_PENDING_INTENTS"
             else -> e.localizedMessage as String
         }
       }
      return e.localizedMessage as String
    }



}