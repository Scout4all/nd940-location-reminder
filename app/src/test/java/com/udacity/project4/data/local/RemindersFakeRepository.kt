/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RemindersFakeRepository : ReminderDataSource {
    val remindersDao: MutableList<ReminderDTO> = mutableListOf()
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private var hasErrors = false
    fun setForceError() {
        this.hasErrors = true

    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            return@withContext try {
                if (hasErrors) {
                    throw Exception("Exception")
                }
                Result.Success(remindersDao)
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)

            }
        }

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                remindersDao.add(reminder)
            }
        }
    }


    override suspend fun getReminder(reminderId: String): Result<ReminderDTO> =
        withContext(ioDispatcher) {
            wrapEspressoIdlingResource {
                return@withContext try {
                    if (hasErrors) {
                        throw Exception("Exception")
                    }
                    Result.Success(remindersDao.filter { it.id == reminderId }.single())
                } catch (ex: Exception) {
                    Result.Error(ex.localizedMessage)

                }
            }
        }

    override suspend fun deleteAllReminders() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                remindersDao.clear()
            }
        }
    }

    override suspend fun deleteReminder(id: String) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                remindersDao.removeAt(0)
            }
        }
    }


}