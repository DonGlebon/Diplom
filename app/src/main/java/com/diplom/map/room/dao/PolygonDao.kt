package com.diplom.map.room.dao

import androidx.room.*
import com.diplom.map.room.data.SubFeatureData
import com.diplom.map.room.entities.SubFeature
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PolygonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(subFeature: SubFeature): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(subFeatures: List<SubFeature>): Single<List<Long>>

    @Query("SELECT * FROM SubFeatures WHERE uid in (:subFeatureId)")
    fun getPolygonsById(subFeatureId: List<Long>): Flowable<SubFeature>

    @Transaction
    @Query("SELECT FeatureID, uid FROM SubFeatures WHERE uid IN (:polygonsIDs) ORDER BY uid")
    fun getPolygonsWithPoints(polygonsIDs: List<Long>): Flowable<List<SubFeatureData>>

}