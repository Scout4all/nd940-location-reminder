package com.udacity.project4

import android.app.Application
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry

import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.*
import com.google.android.apps.common.testing.accessibility.framework.replacements.TextUtils
import com.udacity.project4.data.FakeData
import com.udacity.project4.locationreminders.ReminderDescriptionViewModel
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

//Grant location Permission before test
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
            single {
                ReminderDescriptionViewModel(
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

    //clean database after finish test
    @After
        fun resetDatabase() = runBlocking{
        repository.deleteAllReminders()
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
    @Test
    fun addReminderTest_withInputErrors_ResultShowToastSnakeBar() {
        val reminderData = FakeData.remindersDTOList.get(0)
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withId(R.id.addReminderFAB)).perform(click())

        //check empty title save button
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText(R.string.err_enter_title)).check(matches(isDisplayed()))

//check select location with empty title
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withText(R.string.err_enter_title)).check(matches(isDisplayed()))

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

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
         device.pressMenu()
            onView(withText(R.string.delete_all_notes)).perform(click())
            //check if no data is displayed
            onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        activityScenario.close()
    }
    @Test
    fun openAppWhileNoLocationService_enableLocationFromAppAndSaveReminder(){
        locationHelper()
        val reminderData = FakeData.remindersDTOList.get(0)

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        val allowGpsBtn = device.findObject(
            UiSelector()
                .className("android.widget.Button").packageName("com.google.android.gms")
                .resourceId("android:id/button1")
                .clickable(true).checkable(false)
        )
        device.pressDelete() // just in case to turn ON blur screen (not a wake up) for some devices like HTC and some other

        if (allowGpsBtn.exists() && allowGpsBtn.isEnabled) {
            do {
                allowGpsBtn.click()
            } while (allowGpsBtn.exists())
        }

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
        resetLocation()
    }


    private fun locationHelper(){
    if(isLocationEnabled()){
        device.openQuickSettings()
      val notification=   device.findObject(UiSelector().textContains("Location"))
                 notification.click()
        device.pressHome()
             }
    }


   private fun resetLocation(){
        if(!isLocationEnabled()){

            device.openQuickSettings()
            val notification=   device.findObject(UiSelector().textContains("Location"))

            notification.click()
            device.pressHome()
        }
    }

   private fun isLocationEnabled(): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationMode = try {
                Settings.Secure.getInt(appContext.contentResolver, Settings.Secure.LOCATION_MODE)
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                appContext.contentResolver,
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }
}
