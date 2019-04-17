package com.diplom.map.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "Features",
    foreignKeys = [ForeignKey(
        entity = Layer::class,
        parentColumns = ["uid"],
        childColumns = ["LayerID"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("LayerID")]
)
data class Feature(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    @ColumnInfo(name = "LayerID")
    val lid: Long,
    val classcode: String
)


