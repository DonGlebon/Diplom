package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.FeatureData
import com.diplom.map.room.entities.SubFeature

data class FData(
    var uid: Long,
    var LayerID: Long,
    @Relation(
        parentColumn = "uid", entityColumn = "FeatureID",
        entity = SubFeature::class, projection = ["FeatureID", "uid"]
    )
    var subFeatures: List<SubFeatureData>,
    @Relation(
        parentColumn = "uid", entityColumn = "FeatureID",
        entity = FeatureData::class
    )
    var data: List<FDData>
)