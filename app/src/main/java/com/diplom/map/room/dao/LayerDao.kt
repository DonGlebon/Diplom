package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diplom.map.room.data.LayerData
import com.diplom.map.room.entities.Layer
import com.diplom.map.room.entities.LayerVisibility
import io.reactivex.*

@Dao
interface LayerDao {

    @Query("SELECT uid FROM Layers WHERE filename = :filename")
    fun findLayerByName(filename: String): Maybe<Long>

    @Query("SELECT * FROM layers")
    fun getDataLayers(): Observable<List<LayerData>>

    @Query("SELECT * FROM Layers")
    fun getLayers(): Flowable<List<LayerVisibility>>

    @Query("UPDATE Layers SET ZIndex = :zIndex,  minZoom = :minZoom, maxZoom = :maxZoom WHERE uid = :uid")
    fun updateLayerVisibility(uid: Long, zIndex: Int, minZoom: Int, maxZoom: Int): Completable

    @Query("DELETE FROM Layers WHERE uid = :uid")
    fun deleteLayer(uid: Long): Completable


    // @Query("SELECT Layers.uid, FROM LAYERS")

}