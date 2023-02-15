package com.udacity.project4.locationreminders.geofence

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573

        //        TODO: call this to start the JobIntentService to handle the geofencing transition events
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        //TODO: handle the geofencing transition events and
        // send a notification to the user when he enters the geofence area
        //TODO call @sendNotification

        //TODO: implement the onReceive method to receive the geofencing events at the background
        val geofencingEvent : GeofencingEvent = GeofencingEvent.fromIntent(intent)!!
        if(geofencingEvent.hasError()){
            Timber.e("error ${geofencingEvent.errorCode}")
            return
        }
//        geofencingEvent.triggeringGeofences?.clear()
        val geofenceList : MutableList<Geofence>? = geofencingEvent.triggeringGeofences
        sendNotification(geofencingEvent.triggeringGeofences!!)
        for(geofence in geofenceList!!){
            Timber.e ( geofence.requestId)
        }
        val location = geofencingEvent.triggeringLocation

        val transition = geofencingEvent.geofenceTransition
        when(transition){
            Geofence.GEOFENCE_TRANSITION_ENTER ->{
                // Creating and sending notification

//                notificationManager.sendNotification(context,"entered")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT ->{

            }
        }
    }

    //TODO: get the request id of the current geofence
    private fun sendNotification(triggeringGeofences: List<Geofence>) {
        val requestId = ""

        //Get the local repository instance
        val remindersLocalRepository: ReminderDataSource by inject()
//        Interaction to the repository has to be through a coroutine scope
        CoroutineScope(coroutineContext).launch(SupervisorJob()) {
            //get the reminder with the request id
            val result = remindersLocalRepository.getReminder(requestId)
            if (result is Result.Success<ReminderDTO>) {
                val reminderDTO = result.data
                //send a notification to the user with the reminder details
                sendNotification(
                    this@GeofenceTransitionsJobIntentService, ReminderDataItem(
                        reminderDTO.title,
                        reminderDTO.description,
                        reminderDTO.location,
                        reminderDTO.latitude,
                        reminderDTO.longitude,
                        reminderDTO.id
                    )
                )
            }
        }
    }

}
