package com.diplom.map.mvp.config.di

import com.diplom.map.mvp.components.layerscreen.backstage.LayerScreenModule
import com.diplom.map.mvp.components.layerscreen.presenter.LayerScreenPresenter
import com.diplom.map.mvp.components.layerscreen.view.LayerActivity
import com.diplom.map.mvp.components.mapscreen.backstage.MapScreenModule
import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import com.diplom.map.mvp.components.mapscreen.view.MapActivity
import com.diplom.map.room.backstage.AppDatabaseModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [MapScreenModule::class, LayerScreenModule::class, AppDatabaseModule::class])
@Singleton
interface AppDiComponent {

    fun inject(mapScreenActivity: MapActivity)

    fun inject(layerScreenActivity: LayerActivity)


    fun inject(layerScreenPresenter: LayerScreenPresenter)

    fun inject(mapScreenPresenter: MapScreenPresenter)
}