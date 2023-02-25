/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object ServiceLocator {
    var auth = Firebase.auth

}