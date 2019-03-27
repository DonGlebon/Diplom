package com.diplom.map.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    foreignKeys = [ForeignKey(
        entity = Polygon::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("pid"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index(value = ["pid", "Lat", "Lng"], unique = true)]
)
data class Point(
    @PrimaryKey(autoGenerate = true) var uid: Long,
    val pid: Long,
    @ColumnInfo(name = "Lat")
    var lat: Double,
    @ColumnInfo(name = "Lng")
    var lng: Double
)