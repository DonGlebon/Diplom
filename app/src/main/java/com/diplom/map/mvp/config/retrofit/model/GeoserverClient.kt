package com.diplom.map.mvp.config.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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