package com.diplom.map.mvp.config.di

import com.diplom.map.mvp.components.mapscreen.backstage.MapScreenModule
import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import com.diplom.map.mvp.components.mapscreen.view.MapActivity
import com.diplom.map.mvp.config.room.backstage.AppDatabaseModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [MapScreenModule::class])
@Singleton
interface AppDiComponent {
    fun inject(mapScreenActivity: MapActivity)
}