/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.reminderslist


import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.data.FakeData
import com.udacity.project4.data.local.RemindersFakeRepository
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
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
    private lateinit var remindersFakeRepository: RemindersFakeRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        geoFenceHelper = GeoFenceHelper(appContext)
        remindersFakeRepository = RemindersFakeRepository()
        viewModel = RemindersListViewModel(appContext, remindersFakeRepository, geoFenceHelper)

        insertReminders()
    }

    private fun insertReminders() = mainCoroutineRule.runTest {
        FakeData.remindersDTOList.forEachIndexed { index, reminderDataItem ->
            remindersFakeRepository.saveReminder(reminderDataItem)
        }
    }

    @Test
    fun loadReminders_getRemindersList_returnNotEmptyListWithSameDatasourceSizeAndShowLoadingView() =
        mainCoroutineRule.runBlockingTest {
            //Given

            mainCoroutineRule.pauseDispatcher()
            //when
            viewModel.loadReminders()
            var showLoading = viewModel.showLoading.getOrAwaitValue()

            //Check if loading is showing
            assertThat(showLoading, `is`(true))

            mainCoroutineRule.resumeDispatcher()
            //Then get reminders list
            val result = viewModel.remindersList.getOrAwaitValue()
            //check show loading view is gone
            showLoading = viewModel.showLoading.getOrAwaitValue()
            assertThat(showLoading, `is`(false))
            //check if result is not empty
            assertThat(result.isNotEmpty(), `is`(true))
            assertThat(result.size, `is`(remindersFakeRepository.remindersDao.size))
        }

    @Test
    fun checkLoading_returnTrueWhenLoadThenFalse() = mainCoroutineRule.runBlockingTest {

        viewModel.loadReminders()
        val result = viewModel.showLoading.getOrAwaitValue()
        assertThat(result, `is`(false))

    }


    @Test
    fun deleteAllRemindersOrReminderListIsEmpty_returnEmptyReminderListAndNoData() =
        mainCoroutineRule.runBlockingTest {

            //when
            viewModel.deleteAllReminders()

            //Then
            val result = viewModel.remindersList.getOrAwaitValue()

            assertThat(result.isEmpty(), `is`(true))
            assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
        }

    @Test
    fun deleteItem_shouldReturnSizeLessThanLoaded() = mainCoroutineRule.runBlockingTest {

        //Given item to delete
        val itemToDelete = FakeData.remindersDTOList.get(0)
        //when load reminders
        viewModel.loadReminders()

        //delete item
        viewModel.deleteItem(itemToDelete.id)
        //then
        //get reminders list  new size
        val remindersList = viewModel.remindersList.getOrAwaitValue()
        //check that reminders list not have deleted item
        assertThat(remindersList, not(hasItem(itemToDelete)))

    }

    @Test
    fun loadReminders_DataSourceError_SnakeBarIsShown() = mainCoroutineRule.runBlockingTest {
        //Given
        remindersFakeRepository.setForceError()

        //when
        viewModel.loadReminders()

        //Then
        val result = viewModel.showSnackBar.getOrAwaitValue()

        assertThat(result, `is`("Exception"))

    }

}