package com.diplom.map.mvp.config.room

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity
data class MultiPolygon(
    @PrimaryKey
    var uid: Int
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = MultiPolygon::class,
        parentColumns = ["uid"],
        childColumns = ["mpid"],
        onDelete = CASCADE
    )]
)
data class Polygon(
    @PrimaryKey
    val uid: Int,
    val mpid: Int
)


@Entity(
    foreignKeys = [ForeignKey(
        entity = Polygon::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("pid"),
        onDelete = CASCADE
    )],
    indices = [Index(value = ["Lat", "Lng"])]
)
data class Point(
    @PrimaryKey var uid: Int,
    val pid: Int,
    @ColumnInfo(name = "Lat") var lat: Double,
    @ColumnInfo(name = "Lng") var lng: Double
)

