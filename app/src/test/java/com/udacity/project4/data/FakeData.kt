package com.udacity.project4.data

import androidx.annotation.VisibleForTesting
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
object FakeData {

      val reminder1 = ReminderDTO("Work there",
        "one day i will go there","Google Dubai",25.1003948,55.162867,
        "ChIJAAAAAAAAAAARd8D2Azy-09Q")
    val reminder2= ReminderDTO("Say Hi to Friend",
        "say hi to other good developer there","Sajilni.com - Event Management Solution Event ticket seller",
        25.095428,55.160083,
        "ChIJAAAAAAAAAAARrShgG2SUNc0")

    val reminder3= ReminderDTO("Meet Friend",
        "daily friend meeting at evening","Caribou Coffee",
        25.104943,55.168110,
        "ChIJAAAAAAAAAAARfyitG2mPJTc")



    //
    val reminderDataItem1 = ReminderDataItem("Work there",
        "one day i will go there","Google Dubai",25.1003948,55.162867,
        "ChIJAAAAAAAAAAARd8D2Azy-09Q")
    val reminderDataItem2= ReminderDataItem("Say Hi to Friend",
        "say hi to other good developer there","Sajilni.com - Event Management Solution Event ticket seller",
        25.095428,55.160083,
        "ChIJAAAAAAAAAAARrShgG2SUNc0")
    val  reminderDataNullTitle= ReminderDataItem(null,
        "daily friend meeting at evening","Caribou Coffee",
        25.104943,55.168110,
        "ChIJAAAAAAAAAAARfyitG2mPJTc")

    val  reminderDataNullLocation= ReminderDataItem("Say Hi to Frien",
        "daily friend meeting at evening",null,
        25.104943,55.168110,
        "ChIJAAAAAAAAAAARfyitG2mPJTc")

    val remindersList = listOf(
        reminder1,reminder2
    ).sortedBy { it.title }.toMutableList()



 }
