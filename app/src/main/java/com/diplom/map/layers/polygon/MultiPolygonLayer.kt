package com.diplom.map.layers.polygon

import android.graphics.Color
import android.util.Log
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.ESRIPoly
import com.bbn.openmap.layer.shape.ESRIPolygonRecord
import com.bbn.openmap.layer.shape.ESRIRecord
import com.bbn.openmap.layer.shape.ShapeFile
import com.diplom.map.layers.GEOLayer
import com.diplom.map.layers.utils.DbfReader
import com.diplom.map.layers.utils.VisibleTask
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class MultiPolygonLayer(filename: String, path: String) :
    GEOLayer<MultiPolygonLayer> {

    private var polygons: ArrayList<Polygon> = ArrayList()
    private var multiPolygons = ArrayList<ShapeMultiPolygon>()

    init {
        readShpFile(File("$path$filename.shp"))
        readDbfFile(File("$path$filename.dbf"))
    }

    private fun readShpFile(file: File) {
        try {
            val shapeFile = ShapeFile(file)
            var record: ESRIRecord? = shapeFile.nextRecord
            while (record != null) {
                if (ShapeUtils.getStringForType(record.shapeType) == "POLYGON") {
                    val polyRec = record as ESRIPolygonRecord
                    val multiPolygon = ShapeMultiPolygon()
                    for (i in polyRec.polygons.indices) {
                        val poly = polyRec.polygons[i] as ESRIPoly.ESRIFloatPoly
                        val polygon = ShapePolygon()
                        for (j in 0 until poly.nPoints)
                            polygon.points.add(LatLng(poly.getY(j), poly.getX(j)))
                        multiPolygon.polygons.add(polygon)
                    }
                    multiPolygons.add(multiPolygon)
                }
                record = shapeFile.nextRecord
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readDbfFile(file: File) {
        val dbfStream = DbfReader(FileInputStream(file))
        for (i in 0 until dbfStream.records.size)
            multiPolygons[i].attributeSet = dbfStream.records[i] as ArrayList<Any>
        val p = 3
    }

    override fun updateVisibility(bounds: LatLngBounds) {
//        val s: Long = System.currentTimeMillis()
//        Observable.fromIterable(polygons)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.newThread())
//            .doOnNext {
//                var visible = false
//                for (point in it.points) {
//                    if (bounds.contains(point)) {
//                        visible = true
//                        break
//                    }
//                }
//                if (visible != it.isVisible) {
//                    it.isVisible = visible
//                }
//            }
//            .doOnComplete { Log.d("Hello", "com: ${System.currentTimeMillis() - s}") }
//            .subscribe()
        for (i in 0 until polygons.size) {
            val task = VisibleTask()
            task.polygon = polygons[i]
            task.bounds = bounds
            task.execute(polygons[i].points as ArrayList<LatLng>?)
        }

    }

    override fun getLayout(map: GoogleMap): MultiPolygonLayer {
        draw(map)
        return this
    }

    private fun draw(map: GoogleMap) {
        Observable.fromIterable(multiPolygons)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .doOnNext {
                for (polygon in it.polygons) {
                    polygons.add(
                        map.addPolygon(
                            PolygonOptions()
                                .addAll(polygon.points)
                                .strokeColor(Color.argb(150, 20, 240, 40))
                                .fillColor(Color.argb(150, 140, 20, 140))
                                .strokeWidth(4.0f)
                                .visible(true)
                                .zIndex(0f)
                        )
                    )
                }
            }
            .subscribe()
//        for (multiPolygon in multiPolygons) {
//            for (polygon in multiPolygon.polygons) {
//                polygons.add(
//                    map.addPolygon(
//                        PolygonOptions()
//                            .addAll(polygon.points)
//                            .strokeColor(Color.argb(150, 20, 240, 40))
//                            .fillColor(Color.argb(150, 140, 20, 140))
//                            .strokeWidth(4.0f)
//                            .visible(false)
//                            .zIndex(0f)
//                    )
//                )
//            }
//        }
    }
}