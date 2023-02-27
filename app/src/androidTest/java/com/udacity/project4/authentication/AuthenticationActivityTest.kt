/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.authentication

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class AuthenticationActivityTest : KoinTest {

    @Before
    fun init() {
        FirebaseAuth.getInstance().signOut()
        stopKoin()//stop the original app koin
    }

    @Test
    fun loginWithEmailFlowTest() {
        val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)

        onView(withId(R.id.login_button)).perform(click())

        onView(withId(R.id.email_login_button)).perform(click())
        onView(withHint("Email")).perform(typeText("a@b.com"))
        closeSoftKeyboard()
        onView(withText("Next")).perform(click())
        Thread.sleep(3000)

        onView(withHint("Password")).check(matches(isDisplayed()))
        onView(withHint("Password")).perform(typeText("123456"))
        onView(allOf(withId(com.firebase.ui.auth.R.id.button_done), withText("Sign in"))).perform(
            click()
        )

        activityScenario.close()

    }

    @Test
    fun checkIfLoginButtonsAreDisplayed() {
        val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)

        onView(withId(R.id.login_button)).perform(click())

        onView(withId(R.id.email_login_button)).check(matches(isDisplayed()))
        onView(withId(R.id.google_login_button)).check(matches(isDisplayed()))


        activityScenario.close()

    }

}