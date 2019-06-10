package com.diplom.map.room.dao

import androidx.room.*
import com.diplom.map.room.data.*
import com.diplom.map.room.entities.FeatureData
import com.diplom.map.room.entities.Marker
import com.diplom.map.room.entities.ThemeStyleValues
import io.reactivex.*

@Dao
interface LayerDao {

    @Query("SELECT uid FROM Layers WHERE filename = :filename")
    fun findLayerByName(filename: String): Maybe<Long>

    @Query("UPDATE Layers set isVisible = :visible WHERE uid = :layerId")
    fun updateLayerVisible(visible: Boolean, layerId: Long): Completable

    @Transaction
    @Query("SELECT uid,ZIndex,minZoom,maxZoom,GeometryType,themeId FROM layers")
    fun getDataLayers(): Single<List<LayerData>>

    @Transaction
    @Query("Select * From FeaturesData Where FeatureID = :id")
    fun getFeatureData(id: Long): Single<List<FDData>>

    @Transaction
    @Query("Select * From SubFeatures Where FeatureID = :id")
    fun getSubFeatures(id: Long): Single<List<SubFeatureData>>

    @Transaction
    @Query("SELECT * FROM Layers")
    fun getLayers(): Observable<List<LayerVisibility>>

    @Query("UPDATE Layers SET ZIndex = :zIndex,  minZoom = :minZoom, maxZoom = :maxZoom WHERE uid = :uid")
    fun updateLayerVisibility(uid: Long, zIndex: Int, minZoom: Int, maxZoom: Int): Completable

    @Query("DELETE FROM Layers WHERE uid = :uid")
    fun deleteLayer(uid: Long): Completable

    @Query("SELECT filename FROM Layers")
    fun getLayerNames(): Maybe<List<String>>

    @Query("SELECT DISTINCT ColumnName FROM FeaturesData WHERE layername = :layername")
    fun getColumnNamesByLayer(layername: String): Maybe<List<String>>

    @Query("SELECT DISTINCT * FROM FeaturesData WHERE layername = :layername AND ColumnName = :columnName")
    fun getColumnValues(columnName: String, layername: String): Maybe<List<FeatureData>>


    @Transaction
    @Query("SELECT * FROM ThemeStyle WHERE columnName = :columnName AND layerId = (SELECT uid FROM LAYERS WHERE filename = :layerName) LIMIT 1")
    fun getThemeValues(layerName: String, columnName: String): Maybe<ThemeStyleData>

    @Update
    fun updateThemeValue(themeStyleValuesData: ThemeStyleValues): Single<Int>

    @Query("UPDATE Layers SET themeId = :theme WHERE uid = :layer")
    fun updateActiveTheme(layer: Long, theme: Long): Single<Int>

    @Insert
    fun addMarker(marker: Marker): Single<Long>

    @Query("SELECT * FROM Markers WHERE uid = :id LIMIT 1")
    fun getMarker(id: Long): Single<Marker>

    @Query("UPDATE MARKERS set title = :title, snippet = :snippet WHERE uid = :id")
    fun updateMarker(id: Long, title: String, snippet: String): Single<Int>

    @Update
    fun updateMarker(marker: Marker): Single<Int>

    @Delete
    fun deleteMarker(marker: Marker): Single<Int>

    @Query("DELETE FROM MARKERS WHERE uid = :id")
    fun deleteMarker(id: Long): Single<Int>

    @Query("SELECT * FROM MARKERS")
    fun getAllMarkers(): Single<List<Marker>>
}