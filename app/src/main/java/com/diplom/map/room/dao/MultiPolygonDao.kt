package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diplom.map.room.entities.MultiPolygon
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MultiPolygonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(multiPolygon: MultiPolygon): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(multiPolyons: List<MultiPolygon>): Single<List<Long>>

    @Query("Select * FROM MultiPolygon")
    fun getAllMultiPolygons(): Flowable<List<MultiPolygon>>

    @Query("Delete  FROM MultiPolygon WHERE lid = :lid ")
    fun deleteAll(lid: Long): Completable

}

