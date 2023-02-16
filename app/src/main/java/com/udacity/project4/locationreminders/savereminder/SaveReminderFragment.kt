package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.GeoFenceHelper
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenciHelper: GeoFenceHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel
        geofenciHelper = GeoFenceHelper(requireContext())
        geofencingClient = geofenciHelper.geofencingClient
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
            val latLng = _viewModel.selectedPOI.value?.latLng
            val placeId = _viewModel.selectedPOI.value?.placeId

            addGeoFence(latLng,150f,placeId)
            val reminderDataItem = ReminderDataItem(title,description,location,latitude,longitude,placeId)
            _viewModel.validateAndSaveReminder( reminderDataItem)
        }
    }
    @SuppressLint("MissingPermission")
    private fun addGeoFence(latLng: LatLng?, radius: Float, placeId: String?){
        val geofence = geofenciHelper.getGeoFence(placeId!!, latLng!!,radius, Geofence.GEOFENCE_TRANSITION_ENTER)
        val pendingIntent = geofenciHelper.geofenceIntent
        val geofencingRequest = geofenciHelper.geoFencingRequest(geofence)
        geofencingClient.addGeofences(geofencingRequest,pendingIntent)
            .addOnFailureListener {
                val errorMessage = geofenciHelper.errorString(it)
                Timber.e(errorMessage)
            }


    }
    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }
}
