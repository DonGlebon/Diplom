package com.diplom.map.room.dao

import android.graphics.Color
import androidx.room.*
import com.diplom.map.esri.model.ESRILayer
import com.diplom.map.room.entities.*

@Dao
interface GlobalDao {


    @Query("SELECT uid FROM Layers WHERE filename = :filename AND filepath = :filepath")
    fun getLayerWithNameAndPath(filename: String, filepath: String): Long

    @Insert
    fun insertLayerData(layer: Layer): Long

    @Insert
    fun insertMultiPolygon(feature: Feature): Long

    @Insert
    fun insertPolygon(subFeature: SubFeature): Long

    @Insert
    fun insertPoint(points: Point)

    @Insert
    fun insertFeatureData(featureData: FeatureData)

    @Query("SELECT value FROM FeaturesData WHERE lower(ColumnName) like(\"%classcode%\") GROUP BY value")
    fun getClasscodes(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addClasscodeStyle(style: FeatureStyle)

    @Transaction
    fun insertShapeFileData(
        filename: String,
        filepath: String,
        esriLayer: ESRILayer
    ) {

        var layerID = getLayerWithNameAndPath(filename, filepath)
        if (layerID == 0L)
            layerID = insertLayerData(Layer(0, filename, filepath, esriLayer.type))

        for (feature in esriLayer.features) {
            val featureID = insertMultiPolygon(Feature(0, layerID, feature.classcode))
            for (subFeature in feature.features) {
                val subFeatureID = insertPolygon(SubFeature(0, featureID))
                for (point in subFeature.points) {
                    insertPoint(Point(0, subFeatureID, point.latitude, point.longitude))
                }
            }
            for (featureData in feature.featuresData) {
                insertFeatureData(FeatureData(0, featureID, featureData.columnName, featureData.value))
            }
        }
        val classcodes = getClasscodes()
        for (classcode in classcodes)
            addClasscodeStyle(FeatureStyle(0, classcode, Color.argb(180,20,20,250), Color.GREEN, 1f))
    }
}