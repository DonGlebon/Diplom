package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.diplom.map.room.entities.Point
import io.reactivex.Single

@Dao
interface PointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(point: Point): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(points: List<Point>): Single<List<Long>>
}