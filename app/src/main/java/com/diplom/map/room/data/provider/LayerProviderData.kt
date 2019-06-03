package com.diplom.map.room.data.provider

import androidx.room.Ignore

data class LayerProviderData(
    var uid: Long,
    val ZIndex: Int,
    val minZoom: Int,
    val maxZoom: Int,
    var GeometryType: String,
    var themeId: Long
//    var style: ThemeStyleProviderData?,
//    val features: List<FeatureProviderData> = ArrayList()
)