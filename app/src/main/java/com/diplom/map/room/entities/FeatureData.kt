package com.diplom.map.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "FeaturesData",
    foreignKeys = [ForeignKey(
        parentColumns = ["uid"], childColumns = ["FeatureID"],
        entity = Feature::class, onUpdate = CASCADE, onDelete = CASCADE
    ),
        ForeignKey(
            parentColumns = ["filename"], childColumns = ["layername"],
            entity = Layer::class, onUpdate = CASCADE, onDelete = CASCADE
        )],
    indices = [Index("FeatureID"), Index("layername")]
)
data class FeatureData(
    @PrimaryKey(autoGenerate = true)
    var uid: Long,
    var layername: String,
    @ColumnInfo(name = "FeatureID")
    var fid: Long,
    @ColumnInfo(name = "ColumnName")
    var name: String,
    var value: String
)