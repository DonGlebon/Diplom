package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.diplom.map.mvp.components.layerscreen.model.MultiPolygonData
import com.diplom.map.room.entities.Layer
import com.diplom.map.room.entities.MultiPolygon
import com.diplom.map.room.entities.Point
import com.diplom.map.room.entities.Polygon

@Dao
interface GlobalDao {


    @Query("SELECT uid FROM Layer WHERE name = :name AND path = :path")
    fun getLayerWithNameAndPath(name: String, path: String): Long

    @Insert
    fun insertLayerData(layer: Layer): Long

    @Insert
    fun insertMultiPolygon(multiPolygon: MultiPolygon): Long

    @Insert
    fun insertPolygon(polygon: Polygon): Long

    @Insert
    fun insertPoint(points: Point)

    @Transaction
    fun insertShapeFileData(
        filename: String,
        filepath: String,
        layerData: ArrayList<MultiPolygonData>
    ) {

        var layerID = getLayerWithNameAndPath(filename, filepath)
        if (layerID == 0L)
            layerID = insertLayerData(Layer(0, filename, filepath))

        for (multiPolygon in layerData) {
            val mpid = insertMultiPolygon(MultiPolygon(0, layerID))
            for (polygon in multiPolygon.polygons) {
                val pid = insertPolygon(Polygon(0, mpid))
                for (point in polygon.points) {
                    insertPoint(Point(0, pid, point.latitude, point.longitude))
                }
            }
        }
    }
}