/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.project4.domain.ReminderDataItem
import java.util.*

/**
 * Immutable model class for a Reminder. In order to compile with Room
 *
 * @param title         title of the reminder
 * @param description   description of the reminder
 * @param location      location name of the reminder
 * @param latitude      latitude of the reminder location
 * @param longitude     longitude of the reminder location
 * @param id          id of the reminder
 */

@Entity(tableName = "reminders")
data class ReminderDTO(
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "location") var location: String?,
    @ColumnInfo(name = "latitude") var latitude: Double?,
    @ColumnInfo(name = "longitude") var longitude: Double?,
    @PrimaryKey @ColumnInfo(name = "entry_id") var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "notification_id") var notification_id : Int = ((System.currentTimeMillis() % 10000).toInt()),
    )
fun ReminderDTO.asReminderDataItem() : ReminderDataItem{
  return ReminderDataItem(
      title = this.title,
      description = this.description,
      location = this.location,
      latitude = this.latitude,
      longitude = this.longitude,
      id = this.id,
      notification_id = this.notification_id
  )

}

fun List<ReminderDTO>.asReminderDataItemsList() :List< ReminderDataItem>{
    return map{
        ReminderDataItem(
            title = it.title,
            description = it.description,
            location = it.location,
            latitude = it.latitude,
            longitude = it.longitude,
            id = it.id,
            notification_id = it.notification_id
        )
    }


}