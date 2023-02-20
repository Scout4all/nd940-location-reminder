package com.udacity.project4.reminderslist

import android.app.Application
import android.os.Build


import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.data.FakeData

import com.udacity.project4.data.FakeDataSource
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

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var appContext: Application
    private lateinit var geoFenceHelper: GeoFenceHelper
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        geoFenceHelper = GeoFenceHelper(appContext)
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(appContext, fakeDataSource, geoFenceHelper)


    }

    @Test
    fun loadReminders_getRemindersList() = runBlocking {
        //Given
        fakeDataSource.saveReminder(FakeData.reminder1)
        fakeDataSource.saveReminder(FakeData.reminder2)

        //when
        viewModel.loadReminders()

        //Then
        val value = viewModel.remindersList.getOrAwaitValue()

        assertThat(value.isEmpty()).isFalse()
        assertThat(value.size).isEqualTo(5)
    }

    @Test
    fun checkLoading() {
        viewModel.loadReminders()
        val value = viewModel.showLoading.getOrAwaitValue()
        assertThat(value).isFalse()

    }

    @Test
    fun returnError() {
        fakeDataSource.setForceError()
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isNotEmpty()
    }

    @Test
    fun checkIfEmpty() {
         viewModel.loadReminders()
        val value= viewModel.showNoData.getOrAwaitValue()
        assertThat(value).isTrue()
    }

    @Test
    fun deleteRemider_getRemindersList() = runBlocking {
        //Given

        //when
        viewModel.deleteAllReminders()

        //Then
        val value = viewModel.remindersList.getOrAwaitValue()

        assertThat(value.isEmpty()).isTrue()
    }

    @Test
    fun deketeItem() = runBlocking {
        //Given
        fakeDataSource.saveReminder(FakeData.reminder1)
        fakeDataSource.saveReminder(FakeData.reminder2)
        fakeDataSource.saveReminder(FakeData.reminder3)

        viewModel.loadReminders()

     val oldSize =   viewModel.remindersList.getOrAwaitValue().size
        System.out.println("Size Old $oldSize")

        viewModel.deleteItem(FakeData.reminder1.id)


        val newSize = viewModel.remindersList.getOrAwaitValue().size
        System.out.println("Size New $newSize")
        assertThat(newSize).isLessThan(oldSize)

    }

    @Test
    fun shouldReturnError() {
        //when
        viewModel.loadReminders()

        //Then
        val value = viewModel.remindersList.getOrAwaitValue()
        assertThat(value.isEmpty()).isTrue()
    }

}