/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation

import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.data.FakeData
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

    //    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.
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
                RemindersListViewModel(
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
    fun onClickAddReminder_NavigateToAddReminderScreen() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder( )
        )
    }

    @Test
    fun onClickListItem_NavigateToAddReminderScreen() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.reminderssRecyclerView)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText(FakeData.reminderDataItemsList.get(0).title)), click()
            )
        )

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder().setDataItem(FakeData.reminderDataItemsList.get(0))
        )
    }


}