package com.diplom.map.room.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "FeatureStyles", indices = [Index("classcode", unique = true)])
data class FeatureStyle(
    @PrimaryKey(autoGenerate = true)
    var uid: Long,
    var classcode: String,
    var fillColor: Int,
    var strokeColor: Int,
    var strokeWidth: Float
)