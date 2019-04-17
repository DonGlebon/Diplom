package com.diplom.map.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "Layers", indices = [Index(value = ["uid", "filename"], unique = true)])
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
    val maxZoom: Int = 23
)

data class LayerVisibility(
    val uid: Long,
    val filename: String,
    val isVisible: Boolean,
    val ZIndex: Int,
    val minZoom: Int,
    val maxZoom: Int
)
