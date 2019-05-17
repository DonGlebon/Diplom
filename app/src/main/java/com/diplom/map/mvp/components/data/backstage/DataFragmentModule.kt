package com.diplom.map.mvp.components.data.backstage

import com.diplom.map.mvp.components.data.presenter.DataFragmentPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataFragmentModule {

    @Singleton
    @Provides
    fun providePresenter(): DataFragmentPresenter = DataFragmentPresenter()
}