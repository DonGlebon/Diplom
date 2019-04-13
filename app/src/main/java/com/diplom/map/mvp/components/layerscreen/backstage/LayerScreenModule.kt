package com.diplom.map.mvp.components.layerscreen.backstage

import com.diplom.map.mvp.components.layerscreen.presenter.LayerScreenPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LayerScreenModule {

    @Provides
    @Singleton
    fun providePresenter(): LayerScreenPresenter = LayerScreenPresenter()
}