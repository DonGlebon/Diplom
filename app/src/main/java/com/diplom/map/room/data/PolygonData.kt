package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.Point

data class PolygonData(
    var mpid: Long,
    var uid: Long,

    @Relation(parentColumn = "uid", entityColumn = "pid", entity = Point::class)
    var points: List<PointData>
)