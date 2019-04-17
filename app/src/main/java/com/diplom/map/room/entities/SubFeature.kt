package com.diplom.map.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "SubFeatures",
    foreignKeys = [ForeignKey(
        entity = Feature::class,
        parentColumns = ["uid"],
        childColumns = ["FeatureID"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("FeatureID")]
)
data class SubFeature(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    @ColumnInfo(name = "FeatureID")
    val fid: Long
)