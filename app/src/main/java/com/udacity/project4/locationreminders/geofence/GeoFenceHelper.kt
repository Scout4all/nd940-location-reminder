/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.geofence

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.utils.ACTION_GEOFENCE_EVENT
import com.udacity.project4.utils.GEOFENCE_PENDING_INTENT_CODE
import timber.log.Timber

@SuppressLint("InlinedApi", "UnspecifiedImmutableFlag")
class GeoFenceHelper(val base: Application)  :ContextWrapper(base) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(base.applicationContext)

    private val geofenceIntent: PendingIntent by lazy {
        val runningSOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
        val intent = Intent(base.applicationContext, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT

                PendingIntent.getBroadcast(
                    base.applicationContext,
                    GEOFENCE_PENDING_INTENT_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )



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

    fun removeGeofence(geofenceID: String? = null) {
        if (geofenceID.isNullOrEmpty()) {
            geofencingClient.removeGeofences(geofenceIntent).run {
                addOnFailureListener {
                    Timber.d("e ${it.localizedMessage}")
                }
            }
        } else {
            geofencingClient.removeGeofences(listOf(geofenceID)).run {
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