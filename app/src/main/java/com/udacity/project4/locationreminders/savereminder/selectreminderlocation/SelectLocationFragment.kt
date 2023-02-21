package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Bundle

import android.view.*

import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.DEFAULT_ZOOM
import com.udacity.project4.utils.LOCATION_DEFAULT_RADIUS
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var mMapFragment: SupportMapFragment

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var mMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        mMapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mMapFragment.getMapAsync(this);

        return binding.root
    }


    private fun onLocationSelected(map: GoogleMap) {

        map.setOnPoiClickListener { poi ->
            map.clear()
            map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
       saveLocation(poi)

        }


    }


    private fun saveLocation(poi : PointOfInterest){

        _viewModel.latitude.value = poi.latLng.latitude
        _viewModel.longitude.value = poi.latLng.longitude
        _viewModel.selectedPOI.value = poi
        _viewModel.reminderSelectedLocationStr.value = poi.name

        addCircle(poi.latLng)

        val snackbar =
            Snackbar.make(binding.root, "add ${poi.name} to your reminders", Snackbar.LENGTH_LONG)
        snackbar.setAction(getString(R.string.confirm_btn)) {
            if (_viewModel.reminderTitle.value.isNullOrEmpty()) {
                _viewModel.reminderTitle.value = poi.name
            }

            _viewModel.navigationCommand.value = NavigationCommand.Back
        }
        snackbar.show()
    }
private fun addCircle(latLng: LatLng, radius:Float = LOCATION_DEFAULT_RADIUS){
    val circleOptions = CircleOptions()
    circleOptions.radius( radius.toDouble())
    circleOptions.center(latLng)
    circleOptions.strokeColor(Color.argb(255,255,0,0))
    circleOptions.fillColor(Color.argb(64,255,0,0))
    circleOptions.strokeWidth(4f)
    mMap.addCircle(circleOptions)


}
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        getCurrentLocation(googleMap)
        enableMyLocation(googleMap)
        onLocationSelected(googleMap)
        setMapStyle(googleMap)
        setOnMapClick(googleMap)
    }

    private fun setOnMapClick(googleMap: GoogleMap)    {

        var pointOfInterest :PointOfInterest?
            googleMap.setOnMapClickListener {
            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions()
                    .position(it)
                    .title(_viewModel.reminderTitle.value)
            )
                pointOfInterest = PointOfInterest(it,
                    _viewModel.reminderTitle.value!!,
                    _viewModel.reminderTitle.value!!
                )

            saveLocation(pointOfInterest!!)


        }

        pointOfInterest = null

    }



    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

            true
        }
        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

            true
        }
        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED

    }



    @SuppressLint("MissingPermission")
    private fun enableMyLocation(map : GoogleMap) {
        if (isPermissionGranted()) {
            map.setMyLocationEnabled(true)
            getCurrentLocation(map)
        }
    }




    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(map : GoogleMap) {
        var lastKnownLocation: Location?


        val defaultLocation = LatLng(-33.8523341, 151.2106085)

        val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
            if (isPermissionGranted()) {
                val locationResult: Task<Location> = fusedLocationProviderClient.getLastLocation()
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM
                                )
                            )
                        }
                    } else {
                        Timber.d("Current location is null. Using defaults.")
                        Timber.e("Exception: %s", task.exception)
                        map.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM)
                        )
                        map.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.e(e.message, e)
        }
    }

private fun setMapStyle(map: GoogleMap){
    try{
        val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.map_style))
        if(!success){
            Timber.d("Style Parsing failed ")
        }
    }catch (e : Resources.NotFoundException){
        Timber.e(e.message)
    }
}
}
