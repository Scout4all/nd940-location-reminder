package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.utils.ACTION_GEOFENCE_EVENT
import timber.log.Timber

class GeoFenceHelper(base: Application?) : ContextWrapper(base) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(base!!)

    private val geofenceIntent: PendingIntent by lazy {


        val intent = Intent(base, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT

        PendingIntent.getBroadcast(base, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun geoFencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

    }

    private fun getGeoFence(
        id: String,
        latLng: LatLng,
        radius: Float,
        transitionTypes: Int
    ): Geofence {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setRequestId(id)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(1000)
             .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
    }

    fun removeGeofence(geofenceID: List<String> = emptyList()) {
        if (geofenceID.isEmpty()) {
            geofencingClient.removeGeofences(geofenceIntent).run {
                addOnFailureListener {
                    Timber.d("e ${it.localizedMessage}")
                }
            }
        } else {
            geofencingClient.removeGeofences(geofenceID).run {
                addOnFailureListener {
                    Timber.d("e ${it.localizedMessage}")
                }
            }
        }
    }


    private fun errorString(e: Exception): String {
        if (e is ApiException) {
            val apiException = e as ApiException
            return when (apiException.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "GEOFENCE_TOO_MANY_PENDING_INTENTS"
                else -> e.localizedMessage as String
            }
        }
        return e.localizedMessage as String
    }


    @SuppressLint("MissingPermission")
    fun addGeoFence(latitude: Double, longitude: Double, radius: Float = 100f, placeId: String?) {
        val latLng = LatLng(latitude, longitude)

        val geofence = getGeoFence(
            placeId!!,
            latLng!!,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER
        )
        val pendingIntent = geofenceIntent
        val geofencingRequest = geoFencingRequest(geofence)

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnFailureListener {
                val errorMessage = errorString(it)
                Timber.e(errorMessage)
            }


    }

}