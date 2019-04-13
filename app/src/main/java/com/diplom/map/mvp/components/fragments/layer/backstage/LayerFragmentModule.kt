package com.diplom.map.mvp.components.fragments.layer.backstage

import com.diplom.map.mvp.components.fragments.layer.presenter.LayerFragmentPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LayerFragmentModule {

    @Provides
    @Singleton
    fun providePresenter(): LayerFragmentPresenter = LayerFragmentPresenter()

}