package com.diplom.map.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Markers")
data class Marker(
    @PrimaryKey(autoGenerate = true)
    var uid: Long,
    var title: String,
    var snippet: String,
    var lat: Double,
    var lng: Double
)