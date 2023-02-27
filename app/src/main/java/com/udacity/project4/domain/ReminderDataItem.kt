/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.domain

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderDataItem(
    var title: String?,
    var description: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    var id: String? = UUID.randomUUID().toString(),
    var notification_id: Int = ((System.currentTimeMillis() % 10000).toInt()),
) : Serializable

fun ReminderDataItem.asReminderDataObject(): ReminderDTO {
    return ReminderDTO(
        title = this.title,
        description = this.description,
        location = this.location,
        latitude = this.latitude,
        longitude = this.longitude,
        id = this.id!!,
        notification_id = this.notification_id
    )
}