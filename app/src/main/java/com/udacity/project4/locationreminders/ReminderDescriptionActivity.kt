/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthServiceLocator
import com.udacity.project4.authentication.AuthState
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.domain.ReminderDataItem
import com.udacity.project4.utils.INTENT_TO_DESCRIPTION_ACTIVITY
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    private val _viewModel: ReminderDescriptionViewModel by viewModel()
    private var reminderItem: ReminderDataItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLogin()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )

        val intent = getIntent()

        Timber.e(intent.getSerializableExtra(EXTRA_ReminderDataItem).toString())
        if (intent.getSerializableExtra(EXTRA_ReminderDataItem) != null) {

            reminderItem = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem?
        }
        binding.reminderDataItem = reminderItem

    }

    private fun checkLogin() {
        AuthServiceLocator.authState.observe(this) {
            if (it == AuthState.UNAUTHENTICATED) {
                val intent = Intent(this, AuthenticationActivity::class.java)
                intent.putExtra(INTENT_TO_DESCRIPTION_ACTIVITY, true)
                startActivity(intent)

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_delete_reminder -> {
                _viewModel.deleteItem(reminderItem?.id!!)
                val intent = Intent(this, RemindersActivity::class.java)
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
