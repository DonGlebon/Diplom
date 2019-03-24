package com.diplom.map.mvp.config.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MultiPolygonDao {
    @Insert
    fun insert(multiPolygon: MultiPolygon)

    @Query("Select * FROm MultiPolygon")
    fun getAllMultiPolygons(): List<MultiPolygon>
}

@Dao
interface PolygonDao {
    @Insert
    fun insert(polygon: MultiPolygon)
}

@Dao
interface PointDao {
    @Insert
    fun insert(point: Point)

}