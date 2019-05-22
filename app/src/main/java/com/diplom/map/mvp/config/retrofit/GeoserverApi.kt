package com.diplom.map.mvp.config.retrofit

import com.diplom.map.mvp.config.retrofit.model.GeoserverLayers
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface GeoserverApi {
    @GET("rest/workspaces/myws/featuretypes.json")
    fun getLayerNames(): Single<GeoserverLayers>

    @GET("cite/ows?service=WFS&version=1.0.0&request=GetFeature&maxFeatures=5000000&outputFormat=SHAPE-ZIP&srsName=EPSG:4326&format_options=CHARSET%3AUTF-8\"")
    fun getShapeFile(@Query("typeName") layername: String): Single<ResponseBody>

}