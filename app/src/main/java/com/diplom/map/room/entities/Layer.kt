package com.diplom.map.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


@Entity(
    tableName = "Layers",
    indices = [Index(value = ["uid", "filename"], unique = true), Index(
        value = ["filename"],
        unique = true
    ), Index("themeId")],
    foreignKeys = [
        ForeignKey(
            entity = ThemeStyle::class,
            parentColumns = ["uid"],
            childColumns = ["themeId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class Layer(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val filename: String,
    val filepath: String,
    @ColumnInfo(name = "GeometryType")
    val type: String = "Feature",
    val isVisible: Boolean = true,
    val ZIndex: Int = 0,
    val minZoom: Int = 2,
    val maxZoom: Int = 23,
    val themeId: Long? = null
)

