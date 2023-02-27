/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.data.FakeData
import com.udacity.project4.data.local.RemindersFakeRepository
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
@ExperimentalCoroutinesApi
class ReminderDescriptionViewModelTest {
    private lateinit var appContext: Application
    private lateinit var geoFenceHelper: GeoFenceHelper
    private lateinit var viewModel: ReminderDescriptionViewModel
    private lateinit var remindersFakeRepository: RemindersFakeRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        GlobalContext.stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        geoFenceHelper = GeoFenceHelper(appContext)
        remindersFakeRepository = RemindersFakeRepository()
        viewModel =
            ReminderDescriptionViewModel(appContext, remindersFakeRepository, geoFenceHelper)

        insertReminders()
    }

    private fun insertReminders() = mainCoroutineRule.runTest {
        FakeData.remindersDTOList.forEachIndexed { index, reminderDataItem ->
            remindersFakeRepository.saveReminder(reminderDataItem)
        }
    }

    @Test
    fun deleteReminder_showToast() {
        val itemToDelete = FakeData.remindersDTOList.get(0).id
        //when delete reminder
        viewModel.deleteItem(itemToDelete)
        //check if toast triggered
        Truth.assertThat(viewModel.showToast.getOrAwaitValue())
            .isEqualTo(appContext.getString(R.string.reminder_deleted))
    }
}