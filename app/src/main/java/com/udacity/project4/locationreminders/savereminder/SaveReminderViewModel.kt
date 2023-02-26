/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import com.udacity.project4.domain.ReminderDataItem
import com.udacity.project4.domain.asReminderDataObject
import kotlinx.coroutines.launch

class SaveReminderViewModel(
    val app: Application,
    val dataSource: ReminderDataSource,
    private val geoFenceHelper: GeoFenceHelper
) :
    BaseViewModel(app) {

    val dataItem = MutableLiveData<ReminderDataItem?>()
    init {
        dataItem.value = ReminderDataItem(null,null,null,null,null)
    }

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        dataItem.value = null
        dataItem.value = ReminderDataItem(null,null,null,null,null)

    }


    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(addGeofence: Boolean, locationEnabled: Boolean) {
        dataItem.value?.let { reminderData->
            if (validateEnteredData(reminderData)) {

                    geoFenceHelper.addGeoFence(
                        reminderData.latitude!!.toDouble(),
                        reminderData.longitude!!.toDouble(),
                        placeId = reminderData.id.toString()
                    )
                    showToast.value = "Geofence Added"

                saveReminder(reminderData)
            }
        }

    }

    /**
     * Save the reminder to the data source
     */
   private fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(reminderData.asReminderDataObject())
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
 private   fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title

            return false
        }

        if (reminderData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }


    fun confirmAddLocation(latLng: LatLng,placeName:String = "") {
        dataItem.value?.location = if(placeName.isNotEmpty()){
            placeName
        }else{
            "Dropped Marker"
        }

        dataItem.value?.latitude =  latLng.latitude
        dataItem.value?.longitude =  latLng.longitude


        showSnackBarAction.value = "Add ${dataItem.value?.location} as location to your reminder"

    }


}