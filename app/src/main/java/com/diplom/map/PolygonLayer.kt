package com.diplom.map

import android.graphics.Color
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.ESRIPoly
import com.bbn.openmap.layer.shape.ESRIPolygonRecord
import com.bbn.openmap.layer.shape.ESRIRecord
import com.bbn.openmap.layer.shape.ShapeFile
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.io.File

class PolygonLayer(filename: String, path: String) : GEOLayer<PolygonLayer> {

    private lateinit var polygons: ArrayList<Polygon>

    private var _esirPoly: ArrayList<ESIRPolygon> = ArrayList()

    init {
        readShpFile(File("$path$filename.shp"))
    }

    private fun readShpFile(file: File) {
        try {
            val shapeFile = ShapeFile(file)
            var record: ESRIRecord? = shapeFile.nextRecord
            while (record != null) {
                if (ShapeUtils.getStringForType(record.shapeType) == "POLYGON") {
                    val polyRec = record as ESRIPolygonRecord
                    for (i in polyRec.polygons.indices) {
                        val esirPoly = ESIRPolygon()
                        val poly = polyRec.polygons[i] as ESRIPoly.ESRIFloatPoly
                        for (j in 0 until poly.nPoints) {
                            esirPoly.points.add(LatLng(poly.getY(j), poly.getX(j)))
                        }
                        _esirPoly.add(esirPoly)
                    }
                }
                record = shapeFile.nextRecord
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun updateVisibility(bounds: LatLngBounds) {
        for (poly in polygons) {
            var visible = false
            for (point in poly.points) {
                if (bounds.contains(point)) {
                    visible = true
                    break
                }
            }
            if (visible != poly.isVisible)
                poly.isVisible = visible
        }
    }

    override fun getLayout(map: GoogleMap): PolygonLayer {
        val style = PolygonStyle(
            fillColor = Color.argb(150, 20, 240, 40),
            strokeColor = Color.argb(150, 140, 20, 140),
            strokeWidth = 4.0f,
            pattern = arrayListOf(Dash(1f))
        )
        draw(map, style)
        return this
    }

    private fun draw(map: GoogleMap, style: PolygonStyle) {
        polygons = ArrayList()
        for (poly in _esirPoly) {
            val polygonOptions = PolygonOptions()
                .strokeColor(style.strokeColor)
                .fillColor(style.fillColor)
                .strokeWidth(style.strokeWidth)
                .visible(false)
                .zIndex(0f)
            for (point in poly.points)
                polygonOptions.add(point)
            polygons.add(map.addPolygon(polygonOptions))
        }
    }

}