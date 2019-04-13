package com.diplom.map.mvp.components.fragments.map.backstage

import com.diplom.map.mvp.components.fragments.map.presenter.MapFragmentPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class MapFragmentModule {

    @Provides
    @Singleton
    fun providePresenter(): MapFragmentPresenter = MapFragmentPresenter()

}