package com.diplom.map.esri.utils

import android.util.Log
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.*
import com.diplom.map.esri.model.DbfReader
import com.diplom.map.esri.model.ESRIFeature
import com.diplom.map.esri.model.ESRIFeatureData
import com.diplom.map.esri.model.ESRILayer
import com.diplom.map.esri.model.ESRISubFeature
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.FileInputStream

class ShapeFileUtils {
    companion object {

        fun readShapeFile(name: String, path: String): ESRILayer {
            val features = ArrayList<ESRIFeature>()
            var id = 0
            var type = ""
            try {
                val shpFile = ShapeFile(File("${path + name}.shp"))
                val dbfFile = DbfReader(FileInputStream(File("${path + name}.dbf")))
                var record: ESRIRecord? = shpFile.nextRecord
                type = ShapeUtils.getStringForType(record!!.shapeType)
                while (record != null) {
                    val feature = when (ShapeUtils.getStringForType(record.shapeType)) {
                        "POLYGON", "POLYLINE" -> {
                            parseMultiPolygon(record as ESRIPolygonRecord)
                        }
                        "POINT" -> {
                            parsePoint(record as ESRIPointRecord)
                        }
                        else -> {
                            ESRIFeature()
                        }
                    }
                    for (i in 0 until dbfFile.columnCount) {
                        feature.addFeatureData(
                            ESRIFeatureData(
                                dbfFile.columnNames[i],
                                (dbfFile.records[id] as ArrayList<*>)[i].toString()
                            )
                        )
                    }
                    features.add(feature)
                    id++
                    record = shpFile.nextRecord
                }
            } catch (e: Exception) {
                Log.d("Hello", "Read err ${e.message}")
            }
            return ESRILayer(type, features)
        }

        private fun parseMultiPolygon(record: ESRIPolygonRecord): ESRIFeature {
            val feature = ESRIFeature()
            for (i in record.polygons.indices) {
                val polygonRecord = record.polygons[i] as ESRIPoly.ESRIFloatPoly
                val polygon = ESRISubFeature()
                for (j in 0 until polygonRecord.nPoints) {
                    polygon.addPoint(LatLng(polygonRecord.getY(j), polygonRecord.getX(j)))
                }
                feature.addFeature(polygon)
            }
            return feature
        }

        private fun parsePoint(record: ESRIPointRecord): ESRIFeature {
            val future = ESRIFeature()
            future.addFeature(
                ESRISubFeature().also { it.addPoint(LatLng(record.x, record.y)) }
            )
            return future
        }
    }
}