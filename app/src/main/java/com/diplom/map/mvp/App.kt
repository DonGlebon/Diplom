package com.diplom.map.mvp

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.diplom.map.mvp.components.mapscreen.backstage.MapScreenModule
import com.diplom.map.mvp.config.di.AppDiComponent
import com.diplom.map.mvp.config.di.DaggerAppDiComponent
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng

class App : Application() {
    lateinit var injector: AppDiComponent
        private set

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
        injector = DaggerAppDiComponent.builder().mapScreenModule(MapScreenModule()).build()
    }

    companion object {
        private var INSTANCE: App? = null
        @JvmStatic
        fun get(): App = INSTANCE!!
    }
}