package com.diplom.map.mvp.config.di

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class RxModule {

    @Singleton
    @Provides
    fun provideDisposable(): CompositeDisposable = CompositeDisposable()
}