package com.diplom.map.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Layer::class,
        parentColumns = ["uid"],
        childColumns = ["lid"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class MultiPolygon(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val lid: Long
)


