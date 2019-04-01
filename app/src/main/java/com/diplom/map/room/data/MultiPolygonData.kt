package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.Polygon

data class MultiPolygonData(
    var uid: Long,
    var lid: Long,
    @Relation(parentColumn = "uid", entityColumn = "mpid", entity = Polygon::class)
    var polygons: List<PolygonData>
)