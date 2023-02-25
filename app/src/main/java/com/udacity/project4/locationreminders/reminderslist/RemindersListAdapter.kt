/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.R
import com.udacity.project4.base.BaseRecyclerViewAdapter
import com.udacity.project4.domain.ReminderDataItem


//Use data binding to show the reminder on the item
class RemindersListAdapter(callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int): Int {
        return R.layout.it_reminder
    }


}