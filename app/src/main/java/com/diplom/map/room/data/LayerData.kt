package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.MultiPolygon

data class LayerData(
    var uid: Long,
    @Relation(parentColumn = "uid", entityColumn = "lid", entity = MultiPolygon::class)
    var multiPolygons: List<MultiPolygonData>
)