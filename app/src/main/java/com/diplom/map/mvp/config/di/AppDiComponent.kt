package com.diplom.map.mvp.config.di

import com.diplom.map.mvp.components.layerscreen.backstage.LayerScreenModule
import com.diplom.map.mvp.components.layerscreen.view.LayerActivity
import com.diplom.map.mvp.components.mapscreen.backstage.MapScreenModule
import com.diplom.map.mvp.components.mapscreen.view.MapActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [MapScreenModule::class, LayerScreenModule::class])
@Singleton
interface AppDiComponent {

    fun inject(mapScreenActivity: MapActivity)

    fun inject(layerScreenActivity: LayerActivity)
}