/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.udacity.project4.authentication.AuthObserver
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthServiceLocator
import com.udacity.project4.authentication.AuthState
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.databinding.ActivityRemindersBinding


/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemindersBinding

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

checkLogin()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminders)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        NavigationUI.setupActionBarWithNavController(this, navController)

    }
private fun checkLogin( ){
    AuthObserver.authState.observe(this) {
        if (it == AuthState.UNAUTHENTICATED) {
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)

    }





}
