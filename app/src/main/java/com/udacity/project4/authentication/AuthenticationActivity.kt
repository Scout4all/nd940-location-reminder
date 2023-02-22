/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import timber.log.Timber

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        binding.lifecycleOwner = this

        binding.loginButton.setOnClickListener {
            signInFlow()
        }


    }


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }


    override fun onResume() {
        super.onResume()
        //check if user logged in  when activity send results then move to to Reminders activity when login
        checkLogin()
    }

    override fun onStart() {
        super.onStart()
        //check if user logged in then move to to Reminders activity

        checkLogin()
    }

    private fun checkLogin() {
        //FirebaseUserLiveData didn't work here :\ so i have done next :(
        Firebase.initialize(this@AuthenticationActivity)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            val intent = Intent(this, RemindersActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun signInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher_round)
            .setTheme(R.style.AppTheme)
            .setTosAndPrivacyPolicyUrls(
                "https://bigad.me",
                "https://bigad.me"
            )
            .build()

        signInLauncher.launch(signInIntent)

    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Timber.i("Successfully signed in user ${user?.displayName}!")
        } else {

            Timber.i("Sign in unsuccessful ${response?.error?.errorCode}")

        }
    }

}
