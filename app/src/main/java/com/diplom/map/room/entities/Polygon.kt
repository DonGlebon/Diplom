package com.diplom.map.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = MultiPolygon::class,
        parentColumns = ["uid"],
        childColumns = ["mpid"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Polygon(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val mpid: Long
)