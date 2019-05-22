package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.diplom.map.room.entities.Layer

@Dao
interface FeatureDataDao {

    @Query("SELECT * FROM Layers WHERE isVisible = 1")
    fun getVisibleLayers(): List<Layer>

    @Transaction
    fun getDataLayers() {
        val layers = getVisibleLayers()
    }
}