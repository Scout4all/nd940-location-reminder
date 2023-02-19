package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class FirebaseUserLiveData: MutableLiveData<FirebaseUser>() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser
    }



    override fun onActive() {

        firebaseAuth.addAuthStateListener { authStateListener }
    }

    override fun onInactive() {

        firebaseAuth.removeAuthStateListener { authStateListener }
    }
}