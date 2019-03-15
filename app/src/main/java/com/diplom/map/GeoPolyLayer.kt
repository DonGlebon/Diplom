package com.diplom.map

import android.graphics.Color
import android.util.Log
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.ESRIPoly
import com.bbn.openmap.layer.shape.ESRIPolygonRecord
import com.bbn.openmap.layer.shape.ESRIRecord
import com.bbn.openmap.layer.shape.ShapeFile
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import java.io.File

class GeoPolyLayer(override val map: GoogleMap, filename: String, path: String) : GEOLayer {

    override var isVisible: Boolean = true

    private lateinit var polygons: ArrayList<Polygon>

    private var _esirPoly: ArrayList<ESIRPolygon> = ArrayList()

    init {
        readShpFile(File("$path$filename.shp"))
        readDbfFile(File("$path$filename.dbf"))
    }

    fun updateVisibility(bounds: LatLngBounds) {
        for (poly in polygons) {
            poly.isVisible = false
            for (point in poly.points) {
                if (bounds.contains(point)) {
                    poly.isVisible = true
                    break
                }
            }
        }
    }

    override fun setVisibility(visibility: Boolean) {
        isVisible = visibility
        for (poly in polygons) {
            poly.isVisible = visibility
        }
    }

    private fun readShpFile(file: File) {
        var poi = 0
        var pol = 0
        try {
            polygons = ArrayList()
            val shapeFile = ShapeFile(file.absoluteFile)
            var record: ESRIRecord? = shapeFile.nextRecord
            while (record != null) {
                val shapeTypeStr = ShapeUtils.getStringForType(record.shapeType)
                if (shapeTypeStr == "POLYGON") {
                    val polyRec = record as ESRIPolygonRecord
                    for (i in polyRec.polygons.indices) {
                        val esirPoly = ESIRPolygon()
                        val poly = polyRec.polygons[i] as ESRIPoly.ESRIFloatPoly
                        val polygonOptions = PolygonOptions().apply {
                            strokeColor(Color.argb(150, 10, 40, 200))
                            fillColor(Color.argb(150, 10, 200, 40))
                            strokeWidth(2.0f)
                        }
                        pol++
                        for (j in 0 until poly.nPoints) {
                            esirPoly.points.add(LatLng(poly.getY(j), poly.getX(j)))
                            poi++
                            polygonOptions.add(LatLng(poly.getY(j), poly.getX(j)))
                        }
                        polygons.add(map.addPolygon(polygonOptions))
                    }
                }
                record = shapeFile.nextRecord
            }
        } catch (e: Exception) {
            Log.d("Hello", "Error: ${e.message}")
            e.printStackTrace()
        }
        Log.d("Hello", "poi $poi : pol $pol")
    }

    private fun readDbfFile(file: File) {

    }
}