package com.udacity.project4.reminderslist

import android.app.Application
import android.os.Build


import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.data.FakeData

import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import org.robolectric.annotation.Config
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var appContext: Application
    private lateinit var geoFenceHelper: GeoFenceHelper
    private lateinit var remindersListViewModel : RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

@get:Rule
val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        stopKoin()
      appContext = ApplicationProvider.getApplicationContext()
         geoFenceHelper = GeoFenceHelper(appContext)
          fakeDataSource= FakeDataSource()
        remindersListViewModel = RemindersListViewModel(appContext,fakeDataSource,geoFenceHelper)


    }
    @Test
    fun loadReminders_getRemindersList() = runBlocking  {
        //Given
        fakeDataSource.saveReminder(FakeData.reminder1)
        fakeDataSource.saveReminder(FakeData.reminder2)

        //when
        remindersListViewModel.loadReminders()

        //Then
        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(value.isEmpty()).isFalse()
        assertThat(value.size).isEqualTo(5)
    }

    @Test
    fun deleteRemider_getRemindersList() = runBlocking  {
        //Given

        //when
        remindersListViewModel.deleteAllReminders()

        //Then
        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(value.isEmpty()).isTrue()
    }

    @Test
    fun deketeItem() = runBlocking  {
        //Given
//        remindersListViewModel = RemindersListViewModel(appContext,fakeDataSource,geoFenceHelper)
//        val oldSize = fakeDataSource.reminders?.size
//        //when
//        remindersListViewModel.deleteItem(reminder1.id)
//
//        //Then
//        val value = remindersListViewModel.remindersList.getOrAwaitValue()
//
//
//        assertThat(value.size).isLessThan(oldSize)
    }
@Test
    fun shouldReturnError(){
        //when
        remindersListViewModel.loadReminders()

        //Then
        val value = remindersListViewModel.remindersList.getOrAwaitValue()
        assertThat(value.isEmpty()).isTrue()
    }

}