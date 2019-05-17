package com.diplom.map.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Layer::class,
            parentColumns = ["uid"],
            childColumns = ["layerId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class ThemeStyle(
    @PrimaryKey(autoGenerate = true)
    var uid: Long, // id
    var layerId: Long, // слой
    var columnName: String // столбец тематической карты
)