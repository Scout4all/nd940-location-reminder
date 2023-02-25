/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import timber.log.Timber

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by activityViewModel()
    private lateinit var binding: FragmentSaveReminderBinding

    private val args: SaveReminderFragmentArgs by navArgs()
private var isLocationPermissionGranted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        //check if there is passed args to make fragment behavior editing reminder
        if (args.dataItem != null) {
            _viewModel.dataItem.value = args.dataItem
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.onClear()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.selectLocation.setOnClickListener {
            //  Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())

        }

        binding.saveReminder.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }


        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            (permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false)
                    && permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))
            -> {
                isLocationPermissionGranted
            }
            else -> {
                Snackbar.make(
                    binding.root,
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            }
        }
        checkDeviceLocationSettingsAndStartGeofence()
    }



    var locationServiceEnabledResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK ) {
            checkDeviceLocationSettingsAndStartGeofence(false)
        }else{
            Snackbar.make(
                binding.root,
                R.string.location_required_error, Snackbar.LENGTH_LONG
            ).setAction(android.R.string.ok) {
                checkDeviceLocationSettingsAndStartGeofence()
            }.show()
        }
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true)   {
        val locationRequest = LocationRequest.create().apply {
                this.priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution).build()
                    locationServiceEnabledResult.launch(intentSenderRequest)

                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.e("Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_LONG
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        } .addOnCompleteListener {

            _viewModel.validateAndSaveReminder(isLocationPermissionGranted,it.isSuccessful)

        }


    }
}
