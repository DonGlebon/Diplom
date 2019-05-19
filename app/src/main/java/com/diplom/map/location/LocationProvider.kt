package com.diplom.map.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

class LocationProvider(private val Context: Context) {

    private var locationChangeListener: OnLocationChangeListener? = null

    private val locationList = ArrayList<Location>()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(Context)
    private val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 2500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationChangeListener?.onLocationChange(locationResult?.locations)
        }
    }

    init {
        if (ContextCompat.checkSelfPermission(Context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback, null
            )
        }
    }


    fun getLocation(): Task<Location>? {
        return if (ContextCompat.checkSelfPermission(Context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
        } else null
    }

    interface OnLocationChangeListener {
        fun onLocationChange(locations: List<Location>?)
    }

    fun setOnLocationChangeListener(
        method: (loc: List<Location>) -> Unit
    ) {
        locationChangeListener = object :
            OnLocationChangeListener {
            override fun onLocationChange(locations: List<Location>?) {
                locations ?: return
                locationList.addAll(locations)
                method(locationList)
            }
        }
    }
}

@Module
class LocationProviderModule(val Context: Context) {

    @Provides
    @Singleton
    fun provideLocation(): LocationProvider = LocationProvider(Context)
}