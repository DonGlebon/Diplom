package com.diplom.map.room.data

import androidx.room.Relation
import com.diplom.map.room.entities.ThemeStyleValues

data class ThemeStyleData(
    var uid: Long,
    var columnName: String,
    @Relation(
        parentColumn = "uid",
        entityColumn = "themeId",
        entity = ThemeStyleValues::class
    )
    var values: List<ThemeStyleValuesData>
)