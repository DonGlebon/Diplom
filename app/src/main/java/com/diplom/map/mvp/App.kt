package com.diplom.map.mvp

import android.app.Application
import com.diplom.map.location.LocationProviderModule
import com.diplom.map.mvp.components.data.backstage.DataFragmentModule
import com.diplom.map.mvp.components.layer.backstage.LayerFragmentModule
import com.diplom.map.mvp.components.layervisibility.backstage.LayerVisibilityFragmentModule
import com.diplom.map.mvp.components.map.backstage.MapFragmentModule
import com.diplom.map.mvp.config.di.AppDiComponent
import com.diplom.map.mvp.config.di.DaggerAppDiComponent
import com.diplom.map.mvp.config.retrofit.GeoserverClientModule
import com.diplom.map.room.backstage.AppDatabaseModule
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class App : Application() {
    lateinit var injector: AppDiComponent
        private set

    @Inject
    lateinit var disposable: CompositeDisposable

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        injector = DaggerAppDiComponent.builder()
            .layerVisibilityFragmentModule(LayerVisibilityFragmentModule())
            .mapFragmentModule(MapFragmentModule())
            .layerFragmentModule(LayerFragmentModule())
            .dataFragmentModule(DataFragmentModule())
            .geoserverClientModule(GeoserverClientModule())
            .locationProviderModule(LocationProviderModule(this))
            .appDatabaseModule(AppDatabaseModule(this))
            .build()
    }

    companion object {
        private var INSTANCE: App? = null
        @JvmStatic
        fun get(): App = INSTANCE!!
    }

    override fun onTerminate() {
        super.onTerminate()
        disposable.dispose()
    }
}