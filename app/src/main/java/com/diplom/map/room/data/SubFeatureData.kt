package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.Point

data class SubFeatureData(
    var FeatureID: Long,
    var uid: Long,
    @Relation(
        parentColumn = "uid", entityColumn = "SubFeatureID",
        entity = Point::class, projection = ["uid", "Lat", "Lng"]
    )
    var points: List<PointData>
)