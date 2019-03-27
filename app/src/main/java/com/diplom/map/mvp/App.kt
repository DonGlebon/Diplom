package com.diplom.map.mvp

import android.app.Application
import com.diplom.map.mvp.components.layerscreen.backstage.LayerScreenModule
import com.diplom.map.mvp.components.mapscreen.backstage.MapScreenModule
import com.diplom.map.mvp.config.di.AppDiComponent
import com.diplom.map.mvp.config.di.DaggerAppDiComponent
import com.diplom.map.room.backstage.AppDatabaseModule
import com.diplom.map.room.backstage.DaggerDbDiComponent
import com.diplom.map.room.backstage.DbDiComponent

class App : Application() {
    lateinit var injector: AppDiComponent
        private set
    lateinit var dbInjector: DbDiComponent
        private set

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        injector = DaggerAppDiComponent.builder()
            .mapScreenModule(MapScreenModule())
            .layerScreenModule(LayerScreenModule())
            .build()
        dbInjector = DaggerDbDiComponent.builder()
            .appDatabaseModule(AppDatabaseModule(this))
            .build()
    }

    companion object {
        private var INSTANCE: App? = null
        @JvmStatic
        fun get(): App = INSTANCE!!
    }
}