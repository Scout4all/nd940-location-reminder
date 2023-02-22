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
import com.udacity.project4.data.FakeDataSource
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
    private lateinit var fakeDataSource: FakeDataSource

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

        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(appContext, fakeDataSource, geoFenceHelper)
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


}