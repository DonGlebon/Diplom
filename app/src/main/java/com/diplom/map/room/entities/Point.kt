package com.diplom.map.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Polygon::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("pid"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class Point(
    @PrimaryKey(autoGenerate = true) var uid: Long,
    val pid: Long,
    @ColumnInfo(name = "Lat")
    var lat: Double,
    @ColumnInfo(name = "Lng")
    var lng: Double
)