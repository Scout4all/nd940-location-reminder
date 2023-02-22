/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.data.local

import androidx.annotation.VisibleForTesting
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
class FakeDataSource(val reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var hasErrors = false
    fun setForceError() {
        this.hasErrors = true

    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (!hasErrors) {
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
        if (!hasErrors) {
            reminders?.let { remindersList ->
                val reminder = remindersList.filter { reminder ->
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