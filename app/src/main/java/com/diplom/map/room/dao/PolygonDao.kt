package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.diplom.map.room.entities.Polygon
import io.reactivex.Single

@Dao
interface PolygonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(polygon: Polygon): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(polygons: List<Polygon>): Single<List<Long>>

}