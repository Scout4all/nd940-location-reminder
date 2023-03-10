/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.domain.ReminderDataItem
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.asReminderDataItemsList
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import kotlinx.coroutines.launch

import timber.log.Timber

class RemindersListViewModel(
    app: Application,
    private val dataSource: ReminderDataSource,
    private val geoFenceHelper: GeoFenceHelper
) : BaseViewModel(app) {
    // list that holds the reminder data to be displayed on the UI
    val remindersList = MutableLiveData<List<ReminderDataItem>>()

    private val _loadingState = MutableLiveData(true)
    val loadingState = _loadingState

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadReminders() {
        showLoading.value = true
        viewModelScope.launch {
            //interacting with the dataSource has to be through a coroutine
            val result = dataSource.getReminders()
            showLoading.postValue(false)
            _loadingState.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<ReminderDataItem>()
                    dataList.addAll((result.data as List<ReminderDTO>).asReminderDataItemsList())
                    remindersList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }

            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    fun deleteItem(id: String) {
        Timber.e("delete item ${id}")
        viewModelScope.launch {
            dataSource.deleteReminder(id)
            geoFenceHelper.removeGeofence(id)
            showToast.value = "Reminder deleted "
            loadReminders()
        }
    }

    fun deleteAllReminders() {
        viewModelScope.launch {
            dataSource.deleteAllReminders()
            geoFenceHelper.removeGeofence()
            loadReminders()
        }
    }

    /**
     * Inform the user that there's not any data if the remindersList is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = remindersList.value == null || remindersList.value!!.isEmpty()
    }


}