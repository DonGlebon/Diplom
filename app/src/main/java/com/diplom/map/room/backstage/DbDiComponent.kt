package com.diplom.map.room.backstage

import com.diplom.map.mvp.components.layerscreen.presenter.LayerScreenPresenter
import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppDatabaseModule::class])
@Singleton
interface DbDiComponent {

    fun inject(layerScreenPresenter: LayerScreenPresenter)

    fun inject(mapScreenPresenter: MapScreenPresenter)
}