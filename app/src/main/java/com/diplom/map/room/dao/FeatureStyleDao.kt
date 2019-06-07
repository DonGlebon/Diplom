package com.diplom.map.room.dao

import android.graphics.Color
import androidx.room.*
import com.diplom.map.room.entities.ThemeStyleValues

@Dao
interface FeatureStyleDao {

    @Query("SEleCt LayerID FROM Features WHERE uid = :id")
    fun getLayerIdByFeatureId(id: Long): Long

    @Query("UPDATE FeaturesData SET value = :value WHERE ColumnName = :column and FeatureID = :id")
    fun updateFeatureData(id: Long, column: String, value: String)

    @Query("SELECT uid FROM THEMESTYLE WHERE layerId = :lid and columnName = :column")
    fun getThemeId(lid: Long, column: String): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertValue(value: ThemeStyleValues)

    @Transaction
    fun updateFeature(id: Long, map: HashMap<String, String>): Boolean {
        val layerId = getLayerIdByFeatureId(id)
        for (key in map.keys) {
            updateFeatureData(id, key, map[key]!!)
            val themeId = getThemeId(layerId, key)
            insertValue(ThemeStyleValues(0, themeId, map[key]!!, Color.GRAY, Color.DKGRAY, 1f))
        }
        return true
    }
}