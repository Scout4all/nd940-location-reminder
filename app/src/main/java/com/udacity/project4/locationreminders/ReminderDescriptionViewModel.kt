/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import kotlinx.coroutines.launch
import timber.log.Timber

class ReminderDescriptionViewModel(
    app: Application,
    private val dataSource: ReminderDataSource,
    private val geoFenceHelper: GeoFenceHelper
) : BaseViewModel(app) {

    fun deleteItem(id: String) {
        Timber.e("delete item ${id}")
        viewModelScope.launch {
            dataSource.deleteReminder(id)
            geoFenceHelper.removeGeofence(id)
            showToast.value = "Reminder deleted "

        }
    }
}

