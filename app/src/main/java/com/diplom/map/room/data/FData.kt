package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.FeatureStyle
import com.diplom.map.room.entities.SubFeature

data class FData(
    var uid: Long,
    var LayerID: Long,
    var classcode: String,
    @Relation(
        parentColumn = "uid", entityColumn = "FeatureID",
        entity = SubFeature::class, projection = ["FeatureID", "uid"]
    )
    var subFeatures: List<SubFeatureData>,

    @Relation(parentColumn = "classcode", entityColumn = "classcode", entity = FeatureStyle::class)
    var style: List<FeatureStyleData>
)