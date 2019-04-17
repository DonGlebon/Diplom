package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.Feature

data class LayerData(
    var uid: Long,
    val ZIndex: Int,
    val minZoom: Int,
    val maxZoom: Int,
    var GeometryType: String,
    @Relation(
        parentColumn = "uid", entityColumn = "LayerID",
        entity = Feature::class, projection = ["uid", "LayerID", "classcode"]
    )
    var featureList: List<FData>
)