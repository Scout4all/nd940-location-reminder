/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.savereminder


import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.data.FakeData
import com.udacity.project4.data.local.RemindersFakeRepository
import com.udacity.project4.locationreminders.data.dto.asReminderDataItem
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class SaveReminderViewModelTest {

    private lateinit var appContext: Application
    private lateinit var geoFenceHelper: GeoFenceHelper
    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var remindersFakeRepository: RemindersFakeRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        //given
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        geoFenceHelper = GeoFenceHelper(appContext)

        remindersFakeRepository = RemindersFakeRepository()
        viewModel = SaveReminderViewModel(appContext, remindersFakeRepository, geoFenceHelper)
    }

    @Test
    fun validateAndSaveReminder_emptyTitle_showSnakeBarWithErrors() {
        // when save reminder nullable title
        val validateEnteredData = viewModel.validateEnteredData(FakeData.reminderDataNullTitle)
        //Then
        //check is not valid data entered
        assertThat(validateEnteredData).isFalse()
        //check snake bar is shown and displaying message
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)

    }

    @Test
    fun validateAndSaveReminder_nullLocation_showSnakeBarWithErrors() {
        // when save reminder nullable title
        val validateEnteredData = viewModel.validateEnteredData(FakeData.reminderDataNullLocation)
        //Then
        //check is not valid data entered
        assertThat(validateEnteredData).isFalse()
        //check snake bar is shown and displaying message
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)

    }

    @Test
    fun validateAndSaveReminder_validData_isTrue() {
        // when save reminder nullable title
        val validateEnteredData =
            viewModel.validateEnteredData(FakeData.remindersDTOList.get(0).asReminderDataItem())
        //Then
        //check if valid data entered
        assertThat(validateEnteredData).isTrue()
        //check snake bar is shown and displaying message

    }

    @Test
    fun saveReminder_validData_isTrueToast() {
        // when save reminder nullable title
        viewModel.saveReminder(FakeData.remindersDTOList[0].asReminderDataItem())
        //Then
        //check is not valid data entered
        assertThat(viewModel.showToast.getOrAwaitValue()).isEqualTo(appContext.getString(R.string.reminder_saved))

    }

    @Test
    fun saveGeoFence_geofenceAndLocationEnabled_showGeofenceAddedToast() {
        // when save reminder nullable title
        viewModel.dataItem.value = FakeData.remindersDTOList[0].asReminderDataItem()
        //Then
        viewModel.saveGeofence(true, true)
        //check is not valid data entered
        assertThat(viewModel.showToast.getOrAwaitValue()).isEqualTo(appContext.getString(R.string.geofence_added))


    }

    @Test
    fun saveGeoFence_geofenceEnabledAndLocationDisabled_showGeofenceNotAdded() {
        // when save reminder nullable title
        viewModel.dataItem.value = FakeData.remindersDTOList[0].asReminderDataItem()
        //Then
        viewModel.saveGeofence(true, false)
        //check is not valid data entered
        assertThat(viewModel.showToast.getOrAwaitValue()).isEqualTo(appContext.getString(R.string.geofences_not_added))
    }

    @Test
    fun saveGeoFence_geofenceDisabledAndLocationEnabled_showGeofenceNotAdded() {
        // when save reminder nullable title
        viewModel.dataItem.value = FakeData.remindersDTOList[0].asReminderDataItem()
        //Then
        viewModel.saveGeofence(false, true)
        //check is not valid data entered
        assertThat(viewModel.showToast.getOrAwaitValue()).isEqualTo(appContext.getString(R.string.geofences_not_added))
    }
}