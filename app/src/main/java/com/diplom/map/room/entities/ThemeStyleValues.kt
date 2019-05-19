package com.diplom.map.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ThemeStyle::class,
        parentColumns = ["uid"],
        childColumns = ["themeId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("themeId")]
)
data class ThemeStyleValues(
    @PrimaryKey(autoGenerate = true)
    var uid: Long, // id
    var themeId: Long, //К какой теме относится
    var value: String, // для какого значеения
    var fillColor: Int,
    var strokeColor: Int,
    var strokeWidth: Float
)