package com.diplom.map.mvp.config.retrofit

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import javax.inject.Singleton


class GeoserverClient(private val retrofit: Retrofit) {

    fun getApi(): GeoserverApi {
        return retrofit.create(GeoserverApi::class.java)
    }
}

@Module
class GeoserverClientModule {

    @Singleton
    @Provides
    fun provideGeoserverClient(): GeoserverClient =
        GeoserverClient(
            Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        )
}

interface GeoserverApi {
    @GET
    fun getLayerNames(@Url string: String): Single<GeoserverLayers>

    @GET
    fun getShapeFile(@Url string: String): Single<ResponseBody>

}

class GeoserverLayers(
    @SerializedName("featureTypes")
    @Expose
    var featureTypes: FeatureTypes?
)

data class FeatureTypes(

    @SerializedName("featureType")
    @Expose
    var featureType: List<FeatureType>?

)

data class FeatureType(
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("href")
    @Expose
    var href: String
)

//class Post {
//    @SerializedName("userId")
//    @Expose
//    var userId: Int = 0
//    @SerializedName("id")
//    @Expose
//    var id: Int = 0
//    @SerializedName("title")
//    @Expose
//    var title: String? = null
//    @SerializedName("body")
//    @Expose
//    var body: String? = null
//}