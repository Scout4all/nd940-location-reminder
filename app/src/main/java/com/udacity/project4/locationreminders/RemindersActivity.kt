/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.map
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthState
import com.udacity.project4.authentication.FirebaseUserLiveData
import com.udacity.project4.databinding.ActivityRemindersBinding


/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemindersBinding
    private lateinit var navHostFragment: NavHostFragment
//    private val saveReminderViewModel by viewModel<SaveReminderViewModel>()
    val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    //private lateinit var navController : NavController
    lateinit var appBarConfiguration: AppBarConfiguration


    private val navController: NavController by lazy {
        val navHost = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navHost.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminders)
//        if(Firebase.auth.currentUser == null){
//            val intent = Intent(this,AuthenticationActivity::class.java)
//            startActivity(intent)
//        }

//        navController = this.findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        NavigationUI.setupActionBarWithNavController(this, navController)
        authState.observe(this) {
            if (it == AuthState.UNAUTHENTICATED) {
//                Toast.makeText(this, "Not Logged in", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, AuthenticationActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
            } else {
//                Toast.makeText(
//                    this,
//                    "Welcome ${ServiceLocator.auth.currentUser?.displayName}",
//                    Toast.LENGTH_SHORT
//                ).show()

            }
        }
//        Toast.makeText(
//            this,
//            "Welcome ${ServiceLocator.auth.currentUser?.displayName}",
//            Toast.LENGTH_SHORT
//        ).show()

    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)

    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//
//     navHostFragment.navController.popBackStack()
//
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    val authState = FirebaseUserLiveData().map {
        when(it){
            null -> AuthState.UNAUTHENTICATED
            else -> AuthState.AUTHENTICATED
        }
    }


}
