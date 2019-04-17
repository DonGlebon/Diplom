package com.diplom.map.mvp.components.layervisibility.backstage

import com.diplom.map.mvp.components.layervisibility.presenter.LayerVisibilityFragmentPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LayerVisibilityFragmentModule {

    @Provides
    @Singleton
    fun providePresenter(): LayerVisibilityFragmentPresenter = LayerVisibilityFragmentPresenter()

}