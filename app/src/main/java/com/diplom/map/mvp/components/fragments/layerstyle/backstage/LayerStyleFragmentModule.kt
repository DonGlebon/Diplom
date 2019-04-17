package com.diplom.map.mvp.components.fragments.layerstyle.backstage

import com.diplom.map.mvp.components.fragments.layerstyle.presenter.LayerStyleFragmentPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LayerStyleFragmentModule {

    @Singleton
    @Provides
    fun providePresenter(): LayerStyleFragmentPresenter = LayerStyleFragmentPresenter()

}