/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.data.FakeData
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.util.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Unconfined)

        //Inflate database with fake data
        insertReminders()
    }

    private fun insertReminders() = mainCoroutineRule.runTest {
        FakeData.remindersDTOList.forEachIndexed { index, reminderDataItem ->
            //Don't insert first reminder for testing
            if (index != 0) {
                repository.saveReminder(reminderDataItem)
            }
        }
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_getReminders_ResultSuccessHasInsertedItem() =
        mainCoroutineRule.runBlockingTest {
            //Given reminder data
            val reminder = FakeData.remindersDTOList.get(0)

            //When save reminder
            repository.saveReminder(reminder)

            //Then
            //get Reminders from database
            val result = repository.getReminders()
            //Check if result success
            assertThat(result, IsInstanceOf(Result.Success::class.java))
            //Check if database reminders has inserted reminder
            val dbReminder = result as Result.Success
            assertThat(dbReminder.data, hasItem(reminder))
        }


    @Test
    fun getReminderByID_ResultSuccessWithReminderItem() = mainCoroutineRule.runBlockingTest {
        //Given reminder
        val reminder = FakeData.remindersDTOList.get(1)
        //When repository get reminder by id
        val result = repository.getReminder(reminder.id)
        //Then check if result is success
        assertThat(result, IsInstanceOf(Result.Success::class.java))
        val dbReminder = result as Result.Success
        //Then check if result data = given reminder
        assertThat(dbReminder.data, `is`(reminder))

    }

    @Test
    fun getReminderByID_ResultError() = mainCoroutineRule.runBlockingTest {
        //Given reminder not in database
        val reminder = FakeData.remindersDTOList.get(0)
        //When repository get reminder by id
        val result = repository.getReminder(reminder.id)
        //Then check if result is Error
        assertThat(result, IsInstanceOf(Result.Error::class.java))
        val dbReminder = result as Result.Error
        //Then check if result message Reminder not found!
        assertThat(dbReminder.message, `is`("Reminder not found!"))

    }


    @Test
    fun deleteReminderByID_ResultSuccessAndReminderNotExistsInDB() =
        mainCoroutineRule.runBlockingTest {
            //Given reminder not in database
            val reminder = FakeData.remindersDTOList.get(1)
            //When repository delete reminder by id
            repository.deleteReminder(reminder.id)

            //Then
            //Reload reminder from database
            val result = repository.getReminder(reminder.id)
            // check if result is Error
            assertThat(result, IsInstanceOf(Result.Error::class.java))
            val dbReminder = result as Result.Error
            //Then check if result message Reminder not found!
            assertThat(dbReminder.message, `is`("Reminder not found!"))

        }


    @Test
    fun deleteAllReminders_ResultSuccessAndEmptyList() = mainCoroutineRule.runBlockingTest {

        //When repository delete all reminders
        repository.deleteAllReminders()

        //Then
        //Reload reminders from database
        val result = repository.getReminders()

        // check if result is Success
        assertThat(result, IsInstanceOf(Result.Success::class.java))
        val dbReminder = result as Result.Success
        //Then check if result is empty list
        assertThat(dbReminder.data, `is`(emptyList()))

    }

    @Test
    fun getReminders_ResultSuccessSortedByLocation() = mainCoroutineRule.runBlockingTest {
        //Given shadow list sorted by location
        val remindersListShadow = FakeData.remindersDTOList.subList(1, 3).sortedBy { it.location }
        //When load reminders
        val result = repository.getReminders()

        //Then
        // check if result is Success
        assertThat(result, IsInstanceOf(Result.Success::class.java))
        val dbReminder = result as Result.Success

        //Then check if result is sorted by location name
        assertThat(dbReminder.data.sortedBy { it.location }, `is`(remindersListShadow))

    }
}