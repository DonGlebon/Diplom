package com.diplom.map.mvp.components.mapscreen.backstage

import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MapScreenModule {

    @Provides
    @Singleton
    fun providePresenter(): MapScreenPresenter = MapScreenPresenter()

}