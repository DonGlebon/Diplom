package com.diplom.map.room.dao

import android.graphics.Color
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.diplom.map.utils.model.ESRILayer
import com.diplom.map.room.entities.*
import java.util.*
import kotlin.collections.ArrayList

@Dao
interface GlobalDao {


    @Query("SELECT uid FROM Layers WHERE filename = :filename AND filepath = :filepath")
    fun getLayerWithNameAndPath(filename: String, filepath: String): Long

    @Insert
    fun insertLayerData(layer: Layer): Long

    @Insert
    fun insertFeature(feature: Feature): Long

    @Insert
    fun insertSubFeature(subFeature: SubFeature): Long

    @Insert
    fun insertPoint(points: Point)

    @Insert
    fun insertFeatureData(featureData: FeatureData)

    @Insert
    fun insertThemeStyle(theme: ThemeStyle): Long

    @Insert
    fun insertBaseThemeStyleValues(values: ThemeStyleValues): Long

    @Query("UPDATE Layers SET themeId = :themeId WHERE uid =:layerId")
    fun setLayerTheme(themeId: Long, layerId: Long)

    @Transaction
    fun insertShapeFileData(
        filename: String,
        filepath: String,
        esriLayer: ESRILayer
    ) {

        var layerID = getLayerWithNameAndPath(filename, filepath)
        if (layerID == 0L)
            layerID = insertLayerData(Layer(0, filename, filepath, esriLayer.type))
        val values = ArrayList<ArrayList<String>>()
        for (f in esriLayer.features[0].featuresData)
            values.add(ArrayList())
        var featureIds = 0L
        for (feature in esriLayer.features) {
            val featureID = insertFeature(Feature(0, layerID))
            for (subFeature in feature.features) {
                val subFeatureID = insertSubFeature(SubFeature(0, featureID))
                for (point in subFeature.points) {
                    insertPoint(Point(0, subFeatureID, point.latitude, point.longitude))
                }
            }
            for (i in 0 until feature.featuresData.size) {
                insertFeatureData(
                    FeatureData(
                        0,
                        filename,
                        featureID,
                        feature.featuresData[i].columnName,
                        feature.featuresData[i].value
                    )
                )
                values[i].add(feature.featuresData[i].value)
            }
            featureIds++
        }
        val random = Random()
        val colorFill = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255))
        val colorStroke = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255))
        for (i in 0 until esriLayer.features[0].featuresData.size) {
            val column = esriLayer.features[0].featuresData[i].columnName
            val theme = insertThemeStyle(ThemeStyle(0, layerID, column))
            for (value in values[i].distinctBy {
                it.toLowerCase()
            })
                insertBaseThemeStyleValues(ThemeStyleValues(0, theme, value, colorFill, colorStroke, 1f))
        }

    }
}