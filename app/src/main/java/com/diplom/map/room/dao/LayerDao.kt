package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diplom.map.room.entities.Layer
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface LayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(layer: Layer): Single<Long>

    @Insert
    fun insert(layers: List<Layer>): Single<List<Long>>

    @Query("SELECT uid FROM Layer WHERE name = :name")
    fun findLayerByName(name: String): Maybe<Long>


    @Query("SELECT * FROM Layer WHERE uid = :lid LIMIT 1")
    fun getLayerById(lid: Long): Single<Layer>

    @Query("SELECT * FROM layer")
    fun getAll(): Flowable<List<Layer>>

}