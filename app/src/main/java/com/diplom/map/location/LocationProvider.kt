package com.diplom.map.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

class LocationProvider(private val Context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(Context)
    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                Log.d("Hello", "Coords: ${location.latitude}:${location.longitude}")
            }
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


    fun getLocation() {
        if (ContextCompat.checkSelfPermission(Context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation!!.addOnSuccessListener {

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