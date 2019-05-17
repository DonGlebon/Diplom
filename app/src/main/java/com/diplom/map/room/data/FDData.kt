package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.MainBase

data class FDData(
    var uid: Long,
    var layername: String,
    var FeatureID: Long,
    var ColumnName: String,
    var value: String,
    var mainBaseId: Long?,
    @Relation(
        parentColumn = "mainBaseId",
        entityColumn = "numberObject",
        entity = MainBase::class
    )
    var mainData: List<MainBaseData>
)