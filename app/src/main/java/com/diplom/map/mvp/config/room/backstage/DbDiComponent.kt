package com.diplom.map.mvp.config.room.backstage

import com.diplom.map.mvp.components.layerscreen.view.LayerActivity
import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppDatabaseModule::class])
@Singleton
interface DbDiComponent {
    fun inject(mapScreenPresenter: MapScreenPresenter)
    fun inject(layerActivity: LayerActivity)
}