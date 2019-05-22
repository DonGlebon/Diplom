package com.diplom.map.utils

import android.util.Log
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.*
import com.diplom.map.utils.model.ESRIFeature
import com.diplom.map.utils.model.ESRIFeatureData
import com.diplom.map.utils.model.ESRILayer
import com.diplom.map.utils.model.ESRISubFeature
import com.google.android.gms.maps.model.LatLng
import com.linuxense.javadbf.DBFException
import com.linuxense.javadbf.DBFReader
import com.linuxense.javadbf.DBFUtils
import org.cts.CRSFactory
import org.cts.crs.GeodeticCRS
import org.cts.op.CoordinateOperationFactory
import org.cts.registry.EPSGRegistry
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets


class ShapeFileUtils {
    companion object {

        fun readShapeFile(name: String, path: String): ESRILayer {
            val features = ArrayList<ESRIFeature>()
            var id = 0
            var dbfRecords = ArrayList<ArrayList<String>>()
            var dbfFields = ArrayList<String>()
            var type = ""
            try {
                val shpFile = ShapeFile(File("${path + name}.shp"))
                val dbfFile = File("${path + name}.dbf")
                var reader: DBFReader? = null
                try {
                    reader = DBFReader(FileInputStream(dbfFile), StandardCharsets.UTF_8)
                    val numberOfFields = reader.fieldCount
                    for (i in 0 until numberOfFields) {
                        val field = reader.getField(i)
                        dbfFields.add(field.name)
                    }
                    var rowObjects: Array<Any>? = reader.nextRecord()
                    while (rowObjects != null) {
                        val array = ArrayList<String>()
                        for (i in rowObjects.indices) {
                            if (rowObjects[i] != null) {
                                val value = rowObjects[i].toString()
                                if (value.length < 250) {
                                    array.add(rowObjects[i].toString().trim('0', '.'))
                                } else
                                    array.add("")
                            } else
                                array.add("")
                        }
                        dbfRecords.add(array)
                        rowObjects = reader.nextRecord()
                    }
                } catch (e: DBFException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    DBFUtils.close(reader)
                }
                val prjFile = File("${path + name}.prj")
                val cRSFactory = CRSFactory()
                val cRSFactory2 = CRSFactory()
                val registryManager = cRSFactory.registryManager
                registryManager.addRegistry(EPSGRegistry())
                val registryManager2 = cRSFactory2.registryManager
                registryManager2.addRegistry(EPSGRegistry())
                val crsApp = cRSFactory.getCRS("EPSG:4326")
                val crsShp = cRSFactory2.createFromPrj(prjFile)
                crsShp as GeodeticCRS
                crsApp as GeodeticCRS
                var record: ESRIRecord? = shpFile.nextRecord
                type = ShapeUtils.getStringForType(record!!.shapeType)
                while (record != null) {
                    val feature = when (ShapeUtils.getStringForType(record.shapeType)) {
                        "POLYGON", "POLYLINE" -> {
                            parseMultiPolygon(
                                record as ESRIPolygonRecord,
                                crsShp,
                                crsApp
                            )
                        }
                        "POINT" -> {
                            parsePoint(
                                record as ESRIPointRecord,
                                crsShp,
                                crsApp
                            )
                        }
                        else -> {
                            ESRIFeature()
                        }
                    }
                    for (i in 0 until dbfFields.size) {
                        feature.addFeatureData(
                            ESRIFeatureData(
                                dbfFields[i],
                                (dbfRecords[id][i])
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

        private fun parseMultiPolygon(
            record: ESRIPolygonRecord,
            crsShp: GeodeticCRS,
            crsApp: GeodeticCRS
        ): ESRIFeature {
            val feature = ESRIFeature()
            for (i in record.polygons.indices) {
                val polygonRecord = record.polygons[i] as ESRIPoly.ESRIFloatPoly
                val polygon = ESRISubFeature()
                for (j in 0 until polygonRecord.nPoints) {
                    val points = doubleArrayOf(polygonRecord.getX(j), polygonRecord.getY(j))
                    //  coordToWorld(doubleArrayOf(), crsShp, crsApp)
                    polygon.addPoint(LatLng(points[1], points[0]))
                }
                feature.addFeature(polygon)
            }
            return feature
        }

        private fun parsePoint(record: ESRIPointRecord, crsShp: GeodeticCRS, crsApp: GeodeticCRS): ESRIFeature {
//            val points = coordToWorld(doubleArrayOf(record.x, record.y), crsShp, crsApp)
            val future = ESRIFeature()
            future.addFeature(
                ESRISubFeature().also { it.addPoint(LatLng(record.x, record.y)) }
            )
            return future
        }


        fun coordToWorld(coords: DoubleArray, crs1: GeodeticCRS, crs2: GeodeticCRS): DoubleArray {
            val coordOps = CoordinateOperationFactory.createCoordinateOperations(crs1, crs2)
            if (coordOps.size != 0) {
                for (op in coordOps) {
                    return op.transform(coords)
                }
            }
            return doubleArrayOf(0.0, 0.0)
        }
    }


}