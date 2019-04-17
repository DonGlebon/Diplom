package com.diplom.map.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "Points",
    foreignKeys = [ForeignKey(
        entity = SubFeature::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("SubFeatureID"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("SubFeatureID")]
)
data class Point(
    @PrimaryKey(autoGenerate = true) var uid: Long,
    @ColumnInfo(name = "SubFeatureID")
    val sfid: Long,
    @ColumnInfo(name = "Lat")
    var lat: Double,
    @ColumnInfo(name = "Lng")
    var lng: Double
)