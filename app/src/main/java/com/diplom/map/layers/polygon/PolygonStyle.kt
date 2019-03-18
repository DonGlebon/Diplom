package com.diplom.map.layers.polygon

import com.google.android.gms.maps.model.PatternItem

data class PolygonStyle(
    var fillColor: Int,
    var strokeColor: Int,
    var strokeWidth: Float,
    var pattern: ArrayList<PatternItem>
)