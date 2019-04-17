package com.diplom.map.mvp.components.map.backstage

import com.diplom.map.mvp.components.map.presenter.MapFragmentPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class MapFragmentModule {

    @Provides
    @Singleton
    fun providePresenter(): MapFragmentPresenter = MapFragmentPresenter()

}