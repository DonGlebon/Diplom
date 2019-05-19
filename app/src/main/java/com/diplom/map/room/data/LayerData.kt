package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.Feature
import com.diplom.map.room.entities.ThemeStyle

data class LayerData(
    var uid: Long,
    val ZIndex: Int,
    val minZoom: Int,
    val maxZoom: Int,
    var GeometryType: String,
    var themeId: Long?,
    @Relation(
        parentColumn = "uid", entityColumn = "LayerID",
        entity = Feature::class, projection = ["uid", "LayerID"]
    )
    var featureList: List<FData>,
    @Relation(
        parentColumn = "uid",
        entityColumn = "layerId",
        entity = ThemeStyle::class
    )
    var styles: List<ThemeStyleData>
)