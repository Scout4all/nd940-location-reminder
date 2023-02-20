package com.udacity.project4.data

import androidx.annotation.VisibleForTesting
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import timber.log.Timber

//Use FakeDataSource that acts as a test double to the LocalDataSource
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
class FakeDataSource (val reminders : MutableList<ReminderDTO>? = mutableListOf()): ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
    private var hasErrors = false
   fun setHasErrors(){
       this.hasErrors = true

   }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(!hasErrors){
            reminders?.let {
                return Result.Success(ArrayList(it))
            }

        }
        return Result.Error("Reminders not Found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
  if(!hasErrors){
      reminders?.let {remindersList->
          val reminder = remindersList.filter { reminder->
              reminder.id == id
          }.single()
          return Result.Success(reminder)
      }
  }
        return Result.Error("Error in retrieve reminder")
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    override suspend fun deleteReminder(id: String) {
        reminders?.removeAt(0)
    }


}