package com.diplom.map.mvp.components.fragments.layervisibility.backstage

import com.diplom.map.mvp.components.fragments.layervisibility.presenter.LayerVisibilityFragmentPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LayerVisibilityFragmentModule {

    @Provides
    @Singleton
    fun providePresenter(): LayerVisibilityFragmentPresenter = LayerVisibilityFragmentPresenter()

}