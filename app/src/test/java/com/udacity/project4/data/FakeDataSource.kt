package com.udacity.project4.data

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import timber.log.Timber

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource (val reminders : MutableList<ReminderDTO>? = mutableListOf()): ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
       reminders?.let {
           return Result.Success(ArrayList(it))
       }
        return Result.Error("Reminders not Found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        reminders?.let {remindersList->
           val reminder = remindersList.filter { reminder->
                reminder.id == id
            }.single()
             return Result.Success(reminder)
        }
        return Result.Error("Error in retrieve reminder")
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    override suspend fun deleteReminder(id: String) {
      val reminderToRemove = reminders?.filter { it.id == id }?.single()
        Timber.e(reminderToRemove.toString())
      reminders?.remove(reminderToRemove)
    }


}