package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diplom.map.room.entities.Point
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface PointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(point: Point): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(points: List<Point>): Single<List<Long>>

    @Query("SELECT SubFeatureID FROM POINTS WHERE Lat BETWEEN :s AND :n AND Lng BETWEEN :w AND :e GROUP BY SubFeatureID")
    fun getPointsInBounds(n: Double, e: Double, s: Double, w: Double): Maybe<List<Long>>
}