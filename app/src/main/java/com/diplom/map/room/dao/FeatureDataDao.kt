package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.diplom.map.room.data.provider.*

@Dao
interface FeatureDataDao {

    @Query("SELECT uid, ZIndex, minZoom, maxZoom, GeometryType, themeId FROM Layers WHERE isVisible = 1")
    fun getVisibleLayers(): List<LayerProviderData>

    @Query("SELECT columnName FROM ThemeStyle WHERE uid = :themeId LIMIT 1 ")
    fun getStyle(themeId: Long): String

    @Query("SELECT value,fillColor,strokeColor,strokeWidth FROM ThemeStyleValues WHERE themeId = :styleId ORDER BY uid")
    fun getStyleValues(styleId: Long): List<ThemeStyleValuesProviderData>

    @Query("SELECT uid FROM Features WHERE LayerID = :layerId ORDER BY uid")
    fun getFeatures(layerId: Long): List<Long>

    @Query("SELECT uid FROM SubFeatures WHERE FeatureID = :id ORDER BY uid")
    fun getSubFeatures(id: Long): List<Long>

    @Query("SELECT Lat,Lng FROM Points WHERE SubFeatureID = :id ORDER BY uid")
    fun getPoints(id: Long): List<PointProviderData>

    @Query("SELECT columnName, value FROM FeaturesData WHERE FeatureID = :id ORDER BY uid")
    fun getFeatureData(id: Long): List<FeatureDataProviderData>

    @Transaction
    fun getDataLayers(): List<LayerProvider> {
        val layers = ArrayList<LayerProvider>()
        for (layer in getVisibleLayers()) {
            val styleColumn = getStyle(layer.themeId)
            val styleValues = getStyleValues(layer.themeId)
            val features = ArrayList<FeatureProvider>()
            for (fid in getFeatures(layer.uid)) {
                val data = getFeatureData(fid)
                val style =
                    styleValues.findLast { it.value == data.findLast { it.ColumnName == styleColumn }!!.value }!!
                val feature =
                    FeatureProvider(fid, style)
                feature.featureData = data
                for (sfid in getSubFeatures(fid)) {
                    feature.pointList.add(getPoints(sfid))
                }

                features.add(feature)
            }
            layers.add(LayerProvider(layer, features))
        }
        return layers
    }

}