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
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.utils.INTENT_TO_DESCRIPTION_ACTIVITY
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

        val toDescription = getIntent().getBooleanExtra(INTENT_TO_DESCRIPTION_ACTIVITY, false)
        checkLogin(toDescription)
        binding.loginButton.setOnClickListener {
            signInFlow()
        }


    }


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun checkLogin(toDescriptionActivity: Boolean) {
        AuthServiceLocator.authState.observe(this) {
            if (it == AuthState.AUTHENTICATED) {
                if (toDescriptionActivity) {
                    onBackPressed()
                } else {
                    val intent = Intent(this, RemindersActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun signInFlow() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
        )


        val customLayout = AuthMethodPickerLayout.Builder(R.layout.authui_screen)
            .setEmailButtonId(R.id.email_login_button)
            .setGoogleButtonId(R.id.google_login_button)
            .build()

        val signInIntent: Intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAuthMethodPickerLayout(customLayout)
            .setAvailableProviders(providers)

            .build()


        signInLauncher.launch(signInIntent)

    }


/*
     val customLayout = AuthMethodPickerLayout.Builder(R.layout.your_custom_layout_xml)
//            .setGoogleButtonId(R.id.bar)
//            .setEmailButtonId(R.id.foo) // ...
//            .setTosAndPrivacyPolicyId(R.id.baz)
//            .build()
//
//        val signInIntent: Intent = AuthUI.getInstance(this).createSignInIntentBuilder() // ...
//            .setAuthMethodPickerLayout(customLayout)
//            .build()
 */

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

    val authState = FirebaseUserLiveData().map {
        when (it) {
            null -> AuthState.UNAUTHENTICATED
            else -> AuthState.AUTHENTICATED
        }
    }

}
