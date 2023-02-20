//package com.udacity.project4.locationreminders.data.local
//
//import android.app.Application
//import androidx.test.core.app.ApplicationProvider
//import com.udacity.project4.data.FakeDataSource
//import com.udacity.project4.locationreminders.data.ReminderDataSource
//import com.udacity.project4.locationreminders.data.dto.ReminderDTO
//import com.udacity.project4.locationreminders.geofence.GeoFenceHelper
//import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
//import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert.*
//import org.junit.Before
//import org.koin.androidx.viewmodel.dsl.viewModel
//import org.koin.core.context.startKoin
//import org.koin.core.context.stopKoin
//import org.koin.dsl.module
//import org.koin.java.KoinJavaComponent.inject
//
//class RemindersLocalRepositoryTest{
//    val reminder1 = ReminderDTO("Work there",
//        "one day i will go there","Google Dubai",25.1003948,55.162867,
//    "ChIJAAAAAAAAAAARd8D2Azy-09Q")
//    val reminder2= ReminderDTO("Say Hi to Friend",
//        "say hi to other good developer there","Sajilni.com - Event Management Solution Event ticket seller",
//        25.095428,55.160083,
//    "ChIJAAAAAAAAAAARrShgG2SUNc0")
//
//    val reminder3= ReminderDTO("Meet Friend",
//        "daily friend meeting at evening","Caribou Coffee",
//        25.104943,55.168110,
//        "ChIJAAAAAAAAAAARfyitG2mPJTc")
//
//    val reminders = listOf(
//        reminder1,reminder2
//    ).sortedBy { it.title }
//
//    val newReminder = listOf(reminder3)
//    private lateinit var repository: ReminderDataSource
//    private lateinit var appContext: Application
//
//    init {
//        stopKoin()//stop the original app koin
//        appContext = ApplicationProvider.getApplicationContext()
//        val myModule = module {
//            viewModel {
//                RemindersListViewModel(
//                    appContext,
//                    get() as ReminderDataSource,
//                    get()
//                )
//            }
//            single {
//                SaveReminderViewModel(
//                    appContext,
//                    get() as ReminderDataSource ,
//                    get()
//                )
//            }
//            single { RemindersLocalRepository(get()) as ReminderDataSource }
//            single { LocalDB.createRemindersDao(appContext) }
//            single { GeoFenceHelper(appContext) }
//        }
//        //declare a new koin module
//        startKoin {
//            modules(listOf(myModule))
//        }
//        //Get our real repository
//        repository = get()
//
//        //clear the data to start fresh
//        runBlocking {
//            repository.deleteAllReminders()
//        }
//    }
//
//    private lateinit var remiderLocalRepository: RemindersLocalRepository
//    private lateinit var reminderDao :RemindersDao
//    @Before
//    fun createRepo(){
//        val fakeDataSource = FakeDataSource(reminders = reminders.toMutableList())
//
//        remiderLocalRepository = RemindersLocalRepository(reminderDao,Dispatchers.Unconfined)
//    }
//}
