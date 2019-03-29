package com.diplom.map.room.dao

import androidx.room.*
import com.diplom.map.room.data.PolygonData
import com.diplom.map.room.entities.Polygon
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PolygonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(polygon: Polygon): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(polygons: List<Polygon>): Single<List<Long>>

    @Query("SELECT * FROM Polygon WHERE uid in (:uids)")
    fun getPolygonsById(uids: List<Long>): Flowable<Polygon>

    @Transaction
    @Query("SELECT mpid, uid FROM Polygon WHERE uid IN (:polygonsIDs) ORDER BY uid")
    fun getPolygonsWithPoints(polygonsIDs: List<Long>): Flowable<List<PolygonData>>

}