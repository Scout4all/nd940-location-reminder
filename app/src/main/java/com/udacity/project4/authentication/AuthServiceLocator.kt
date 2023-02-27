/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.authentication

import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth

object AuthServiceLocator {
    //to mock in testing class
    var auth = FirebaseAuth.getInstance()

    val authState = FirebaseUserLiveData().map {
        when (it) {
            null -> AuthState.UNAUTHENTICATED
            else -> AuthState.AUTHENTICATED
        }
    }
}