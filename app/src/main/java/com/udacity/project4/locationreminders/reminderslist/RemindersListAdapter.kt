package com.udacity.project4.locationreminders.reminderslist

import android.view.View
import com.udacity.project4.R
import com.udacity.project4.base.BaseRecyclerViewAdapter


//Use data binding to show the reminder on the item
class RemindersListAdapter(   callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int): Int {
        return R.layout.it_reminder
    }




}