package com.diplom.map.mvp.components.layerstyle.backstage

import com.diplom.map.mvp.components.layerstyle.presenter.LayerStyleFragmentPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LayerStyleFragmentModule {

    @Singleton
    @Provides
    fun providePresenter(): LayerStyleFragmentPresenter = LayerStyleFragmentPresenter()

}