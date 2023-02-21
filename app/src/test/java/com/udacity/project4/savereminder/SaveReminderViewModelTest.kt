package com.udacity.project4.savereminder


import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.data.FakeData
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
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
        GlobalContext.stopKoin()
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


    @Test
    fun validateAndSaveReminder_validReminder_validationTrue() = mainCoroutineRule.runBlockingTest{
        // when save reminder nullable Location
       viewModel.validateAndSaveReminder(FakeData.reminderDataItem1)
        // retrieve data from database
        val repoItem=  viewModel.getDataItem(FakeData.reminderDataItem1)

        //Then
        //check  if data saved in database
        assertThat(repoItem).isTrue()

        //check if navigate back to reminders list screen
        assertThat(viewModel.navigationCommand.getOrAwaitValue()).isEqualTo( NavigationCommand.Back)
    }



}