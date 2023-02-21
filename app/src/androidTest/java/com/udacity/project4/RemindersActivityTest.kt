package com.udacity.project4

import android.app.Application
import android.os.Build
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.*
import com.udacity.project4.data.FakeData
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get



@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    KoinTest {// Extended Koin Test - embed autoclose @after method to close Koin after every test


private val device = UiDevice.getInstance(getInstrumentation())

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {

        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource,
                    get()
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource ,
                    get()
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
            single { GeoFenceHelper(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }

        runBlocking {
            FakeData.remindersDTOList.forEachIndexed { index, reminderDataItem ->
                //Don't insert first reminder for testing
                if(index != 0) {
                    repository.saveReminder(reminderDataItem)
                }
            }
        }
    }

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource(): Unit = IdlingRegistry.getInstance().run {
        register(EspressoIdlingResource.countingIdlingResource)
        register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource(): Unit = IdlingRegistry.getInstance().run {
        unregister(EspressoIdlingResource.countingIdlingResource)
        unregister(dataBindingIdlingResource)
    }

//    TODO: add End to End testing to the app

    @Test
    fun addReminderTest_ResultReminderAddedToRemindersList() {
        val reminderData = FakeData.remindersDTOList.get(0)
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withId(R.id.addReminderFAB)).perform(click())


        onView(withId(R.id.reminderTitle)).perform(typeText(reminderData.title))
        onView(withId(R.id.reminderDescription)).perform(typeText(reminderData.description))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(click())

        device.waitForIdle(5000)

           onView(withText("Confirm")).perform(click())

        onView(withId(R.id.saveReminder)).perform(click())
//check if reminder is added
        onView(withText(reminderData.title)).check(matches(isDisplayed()))

        activityScenario.close()
    }


    @Test
    fun editReminder_resultReminderUpdatedInList() {
        val reminderData = FakeData.remindersDTOList.get(1)
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withText(reminderData.title)).perform(click())

        onView(withId(R.id.reminderTitle)).perform(clearText())
        onView(withId(R.id.reminderTitle)).perform(typeText("Hello Google"))
        onView(withId(R.id.reminderDescription)).perform(clearText())
        onView(withId(R.id.reminderDescription)).perform(typeText(" new Description"))
        Espresso.closeSoftKeyboard()


        onView(withId(R.id.saveReminder)).perform(click())
//check if reminder is updated

        onView(withText("Hello Google")).check(matches(isDisplayed()))

        activityScenario.close()
    }


    @Test
    fun deleteAllReminders_noDataViewDisplayed() {
        val reminderData = FakeData.remindersDTOList.get(1)
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext()).run {
            onView(withText(R.string.delete_all_notes)).perform(click())
            //check if no data is displayed
            onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

        }



        activityScenario.close()
    }
   private fun handleConstraints(action: ViewAction, constraints: Matcher<View>): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return constraints
            }

            override fun getDescription(): String {
                return action.description
            }

            override fun perform(uiController: UiController?, view: View?) {
                action.perform(uiController, view)
            }
        }
    }
    private fun grantPermission() {
        val instrumentation = getInstrumentation()
        if (Build.VERSION.SDK_INT >= 23) {
            val allowPermission = UiDevice.getInstance(instrumentation).findObject(
                UiSelector().text(
                    when {
                        Build.VERSION.SDK_INT == 23 -> "Allow"
                        Build.VERSION.SDK_INT <= 28 -> "ALLOW"
                        Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
                        else -> "Only this time"
                    }
                )
            )
            if (allowPermission.exists()) {
                allowPermission.click()
            }
        }
    }

    fun deviceHelper(){


        device.openQuickSettings()
//        device.findObject(
//            UiSelector().textContains("Location").className(
//                ViewGroup::class.java
//            )
//        ).click()

// Set location by setting the latitude, longitude and may be the altitude...
        // Set location by setting the latitude, longitude and may be the altitude...
//        val MockLoc: Array<String> = str.split(",")
//        val location = Location(mocLocationProvider)
//        val lat = java.lang.Double.valueOf(MockLoc[0])
//        location.setLatitude(lat)
//        val longi = java.lang.Double.valueOf(MockLoc[1])
//        location.setLongitude(longi)
//        val alti = java.lang.Double.valueOf(MockLoc[2])
//        location.setAltitude(alti)


    }
}
