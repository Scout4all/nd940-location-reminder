/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData : LiveData<FirebaseUser?>() {

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser
    }

    override fun onActive() {
        AuthServiceLocator.auth.addAuthStateListener(authStateListener)
    }

    override fun onInactive() {
        AuthServiceLocator.auth.removeAuthStateListener(authStateListener)

    }
}

enum class AuthState {
    AUTHENTICATED, UNAUTHENTICATED
}