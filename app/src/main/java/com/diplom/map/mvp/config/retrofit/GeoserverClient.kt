package com.diplom.map.mvp.config.retrofit

import retrofit2.Retrofit


class GeoserverClient(private val retrofit: Retrofit) {

    fun getApi(): GeoserverApi {
        return retrofit.create(GeoserverApi::class.java)
    }
}

