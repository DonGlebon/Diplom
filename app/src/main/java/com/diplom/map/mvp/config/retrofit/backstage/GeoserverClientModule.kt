package com.diplom.map.mvp.config.retrofit.backstage

import com.diplom.map.mvp.config.retrofit.GeoserverClient
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class GeoserverClientModule {

    @Singleton
    @Provides
    fun provideGeoserverClient(): GeoserverClient =
        GeoserverClient(
            Retrofit.Builder()
                .baseUrl("http://nuolh.belstu.by:4201/geoserver/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        )
}