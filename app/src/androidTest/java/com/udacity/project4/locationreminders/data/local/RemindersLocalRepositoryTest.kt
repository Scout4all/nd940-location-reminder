package com.udacity.project4.locationreminders.data.local

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.data.FakeData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
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

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)

        //Inflate database with fake data
        insertReminders()

    }
    private fun insertReminders() = runBlocking {
        FakeData.remindersDTOList.forEachIndexed { index, reminderDataItem ->
            //Don't insert first reminder for testing
            if(index != 0) {
                repository.saveReminder(reminderDataItem)
            }
        }
    }
    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_getReminders_ResultSuccessHasInsertedItem() = runBlocking {
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
        val dbReminder =   result as Result.Success
        assertThat(dbReminder.data, hasItem(reminder))
    }


    @Test
    fun getReminderByID_ResultSuccessWithReminderItem() = runBlocking {
        //Given reminder
        val reminder = FakeData.remindersDTOList.get(1)
        //When repository get reminder by id
        val result = repository.getReminder(reminder.id)
        //Then check if result is success
        assertThat(result, IsInstanceOf(Result.Success::class.java))
     val dbReminder =   result as Result.Success
        //Then check if result data = given reminder
        assertThat(dbReminder.data, `is`(reminder))

    }

    @Test
    fun getReminderByID_ResultError() = runBlocking {
        //Given reminder not in database
        val reminder = FakeData.remindersDTOList.get(0)
        //When repository get reminder by id
        val result = repository.getReminder(reminder.id)
        //Then check if result is Error
        assertThat(result, IsInstanceOf(Result.Error::class.java))
        val dbReminder =   result as Result.Error
        //Then check if result message Reminder not found!
        assertThat(dbReminder.message , `is`("Reminder not found!"))

    }


    @Test
    fun deleteReminderByID_ResultSuccessAndReminderNotExistsInDB() = runBlocking {
        //Given reminder not in database
        val reminder = FakeData.remindersDTOList.get(1)
        //When repository delete reminder by id
         repository.deleteReminder(reminder.id)

        //Then
        //Reload reminder from database
        val result = repository.getReminder(reminder.id)
        // check if result is Error
        assertThat(result, IsInstanceOf(Result.Error::class.java))
        val dbReminder =   result as Result.Error
        //Then check if result message Reminder not found!
        assertThat(dbReminder.message , `is`("Reminder not found!"))

    }


    @Test
    fun deleteAllReminders_ResultSuccessAndEmptyList() = runBlocking {

        //When repository delete all reminders
        repository.deleteAllReminders()

        //Then
        //Reload reminders from database
        val result = repository.getReminders()

        // check if result is Success
        assertThat(result, IsInstanceOf(Result.Success::class.java))
        val dbReminder =   result as Result.Success
        //Then check if result is empty list
        assertThat(dbReminder.data , `is`(emptyList()))

    }

    @Test
    fun getReminders_ResultSuccessSortedByLocation() = runBlocking {
        //Given shadow list sorted by location
        val remindersListShadow = FakeData.remindersDTOList.subList(1,3).sortedBy { it.location }
        //When load reminders
        val result = repository.getReminders()

        //Then
        // check if result is Success
        assertThat(result, IsInstanceOf(Result.Success::class.java))
        val dbReminder =   result as Result.Success

        //Then check if result is sorted by location name
        assertThat(dbReminder.data , `is`(remindersListShadow))

    }
   }