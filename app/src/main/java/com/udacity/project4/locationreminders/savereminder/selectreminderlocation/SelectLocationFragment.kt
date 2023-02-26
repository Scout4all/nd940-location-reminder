/*
 * Copyright (c) 2023.
 * Developed by : Bigad Aboubakr
 * Developer website : http://bigad.me
 * Developer github : https://github.com/Scout4all
 * Developer Email : bigad@bigad.me
 */

package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.DEFAULT_ZOOM
import com.udacity.project4.utils.LOCATION_DEFAULT_RADIUS
import com.udacity.project4.utils.hideKeyboard
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import timber.log.Timber
import java.util.*


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var mMapFragment: SupportMapFragment
    private   val mIsLocationGranted = MutableLiveData(false)

    //Use Koin to get the view model of the SaveReminder

    override val _viewModel: SaveReminderViewModel by activityViewModel<SaveReminderViewModel>()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var mMap: GoogleMap

    val DEFAULT_LOCATION_LATLNG = LatLng(37.4220658, -122.0840907)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        grantLocation()
        mMapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mMapFragment.getMapAsync(this)

        return binding.root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mIsLocationGranted.observe(viewLifecycleOwner) {
            getCurrentLocation(googleMap, it)
        }

        setMapStyle(googleMap)
        onMapClick(googleMap)
        onPoiClicked(googleMap)



    }

    private fun grantLocation() {

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

    }



    private fun onPoiClicked(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            binding.inputsContainer.visibility = View.GONE
            map.clear()
            addMapMarker(map,poi.latLng,poi.name)
            _viewModel.confirmAddLocation(poi.latLng,poi.name)
        }
    }


    private fun onMapClick(googleMap: GoogleMap) {
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            binding.locationNameEt.text.clear()
            binding.inputsContainer.visibility = View.VISIBLE

            val placeName = binding.locationNameEt.text.trim().toString()
            addMapMarker(googleMap,latLng,placeName)
            binding.saveLocationTitleBtn.setOnClickListener {
                binding.inputsContainer.visibility= View.GONE
                it.let { activity?.hideKeyboard(it) }
                _viewModel.confirmAddLocation(latLng)
            }

        }
    }

private fun addMapMarker(map:GoogleMap,latLng:LatLng,title:String){
    map.addMarker(
        MarkerOptions().position(latLng).title(title)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

    )
    addCircle(map, latLng)
}

    private fun addCircle(map: GoogleMap,latLng: LatLng, radius: Float = LOCATION_DEFAULT_RADIUS) {
        val circleOptions = CircleOptions()
        circleOptions.radius(radius.toDouble())
        circleOptions.center(latLng)
        circleOptions.strokeColor(Color.argb(255, 0, 255, 0))
        circleOptions.fillColor(Color.argb(64, 0, 255, 0))
        circleOptions.strokeWidth(4f)
        map.addCircle(circleOptions)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
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


    private fun getCurrentLocation(map: GoogleMap , isLocationGranted : Boolean) {
        var lastKnownLocation: Location?

        val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
                if (isLocationGranted) {
                    map.isMyLocationEnabled = isLocationGranted
                    val locationResult: Task<Location> = fusedLocationProviderClient.lastLocation
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
                                CameraUpdateFactory.newLatLngZoom(
                                    DEFAULT_LOCATION_LATLNG,
                                    DEFAULT_ZOOM
                                )
                            )
                            map.uiSettings.isMyLocationButtonEnabled = isLocationGranted
                        }
                    }

            }

        } catch (e: SecurityException) {
            Timber.e(e.message, e)
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Timber.d("Style Parsing failed ")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e(e.message)
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) &&
                        permissions.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            false
                        )) -> {
                    mIsLocationGranted.value=true
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
        }
    }

}
