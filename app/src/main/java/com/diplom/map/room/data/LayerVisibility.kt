package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.ThemeStyle

data class LayerVisibility(
    val uid: Long,
    val filename: String,
    val isVisible: Boolean,
    val ZIndex: Int,
    val minZoom: Int,
    val maxZoom: Int,
    @Relation(
        parentColumn = "uid",
        entityColumn = "layerId",
        entity = ThemeStyle::class
    )
    var style: List<ThemeStyleData>,
    var themeId: Long

)