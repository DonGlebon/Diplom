package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diplom.map.room.entities.Feature
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MultiPolygonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(feature: Feature): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(multiPolygons: List<Feature>): Single<List<Long>>

    @Query("Select * FROM Features")
    fun getAllMultiPolygons(): Flowable<List<Feature>>

    @Query("Delete  FROM Features WHERE LayerID = :lid ")
    fun deleteAll(lid: Long): Completable

}

