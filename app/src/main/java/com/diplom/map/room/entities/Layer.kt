package com.diplom.map.room.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name", "path"], unique = true)])
data class Layer(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val name: String,
    val path: String
)