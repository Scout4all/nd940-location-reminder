/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.udacity.project4.R
import com.udacity.project4.data.FakeData
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito

class SaveReminderFragmentTest : KoinTest {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        stopKoin()//stop the original app koin

        appContext = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(
            appContext,
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()


        val myModule = module {
            viewModel {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource,
                    get()
                )
            }

            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { database.reminderDao() }
            single { GeoFenceHelper(appContext) }
        }
        //declare a new koin module
        startKoin {
            androidContext(appContext)
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
        insertReminders()
    }

    @After
    fun unSet() = stopKoin()


    private fun insertReminders() = runTest {
        FakeData.remindersDTOList.forEachIndexed { index, reminderDataItem ->
            //Don't insert first reminder for testing
            repository.saveReminder(reminderDataItem)
        }
    }

    @Test
    fun addReminderTest_notSelectedLocationErrors_ResultShowToastSnakeBar() {
        val bundle = Bundle()
        bundle.putParcelable("dataItem", null)
        val scenario = launchFragmentInContainer<SaveReminderFragment>(bundle, R.style.AppTheme)

        Espresso.onView(ViewMatchers.withId(R.id.reminderTitle))
            .perform(ViewActions.typeText(FakeData.remindersDTOList.get(0).title))
        Espresso.closeSoftKeyboard()
        //check empty location when save button
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.err_select_location))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun addReminderTest_emptyTitle_ResultShowToastSnakeBar() {
        val bundle = Bundle()
        bundle.putParcelable("dataItem", null)
        val scenario = launchFragmentInContainer<SaveReminderFragment>(bundle, R.style.AppTheme)
        //check empty title save button
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.err_enter_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onClickSelectLocation_NavigateToSelectLocationScreen() {
        val bundle = Bundle()
        bundle.putParcelable("dataItem", null)
        val scenario = launchFragmentInContainer<SaveReminderFragment>(bundle, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(ViewActions.click())
        Mockito.verify(navController).navigate(
            SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment()
        )
    }
}