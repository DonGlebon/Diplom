package com.diplom.map.room.data.provider

import com.diplom.map.room.data.ThemeStyleValuesData

data class LayerProvider(
    val layerData: LayerProviderData,
    val features: List<FeatureProvider>
//    val styleValue: List<ThemeStyleValuesProviderData>
)

data class FeatureProvider(
    val uid: Long,
    var style: ThemeStyleValuesProviderData,
    val pointList: ArrayList<List<PointProviderData>> = ArrayList(),
    var featureData: List<FeatureDataProviderData> = ArrayList()
    )
