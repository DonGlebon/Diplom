package com.diplom.map.mvp

import android.app.Application
import com.diplom.map.mvp.components.fragments.layer.backstage.LayerFragmentModule
import com.diplom.map.mvp.components.fragments.layervisibility.backstage.LayerVisibilityFragmentModule
import com.diplom.map.mvp.components.fragments.map.backstage.MapFragmentModule
import com.diplom.map.mvp.components.layerscreen.backstage.LayerScreenModule
import com.diplom.map.mvp.components.mapscreen.backstage.MapScreenModule
import com.diplom.map.mvp.config.di.AppDiComponent
import com.diplom.map.mvp.config.di.DaggerAppDiComponent
import com.diplom.map.mvp.config.di.RxModule
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
            .mapScreenModule(MapScreenModule())
            .mapFragmentModule(MapFragmentModule())
            .layerScreenModule(LayerScreenModule())
            .layerFragmentModule(LayerFragmentModule())
            .rxModule(RxModule())
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