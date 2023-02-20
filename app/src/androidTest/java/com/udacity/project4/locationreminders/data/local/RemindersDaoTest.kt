package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.data.FakeData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

     @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()


        insertReminders()

    }

    private fun insertReminders() = runBlockingTest {
        FakeData.remindersDTOList.forEachIndexed { index, reminderDataItem ->
            database.reminderDao().saveReminder(reminderDataItem)
        }
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun deleteReminderByID_returnNullValueWhenQuery() = runBlockingTest {

        val reminder: ReminderDTO = FakeData.remindersDTOList.get(0)

        reminder.let {
            // WHEN - delete reminder from the database.
            database.reminderDao().deleteReminder(it.id)
            // WHEN - Get the reminder by id from the database.
            val loaded = database.reminderDao().getReminderById(it.id)

            // THEN - The loaded data is null
            assertThat(loaded, `is`(nullValue()))

        }


    }


    @Test
    fun insertReminderAndGetById() = runBlockingTest {

        val reminder = FakeData.remindersDTOList.get(0)

        reminder.let {
            // WHEN - Get the reminder by id from the database.
            val loaded = database.reminderDao().getReminderById(it.id)

            // THEN - The loaded data contains the expected values.
            assertThat(loaded as ReminderDTO, notNullValue())
            assertThat(loaded, `is`(it))

        }

    }


    @Test
    fun insertReminderAndDeleteAll_returnEmptyList() = runBlockingTest {

        // WHEN - delete all reminders from the database.
        database.reminderDao().deleteAllReminders()
        // WHEN - Get the reminders by  database.
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded data is is empty list
        assertThat(loaded, `is`(emptyList()))

    }


    @Test
    fun getAllReminders_returnSortedRemindersListByLocation() =
        runBlockingTest {


            // WHEN - Get the reminders from database
            val loaded = database.reminderDao().getReminders()

            // THEN - The loaded data is same as sorted fake data
            assertThat(
                loaded,
                `is`(FakeData.remindersDTOList.sortedBy { it.location })
            )


        }
}