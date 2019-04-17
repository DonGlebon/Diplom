package com.diplom.map.esri.model

data class ESRILayer(
    val type: String,
    val features: ArrayList<ESRIFeature>
)