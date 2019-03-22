package com.diplom.map.layers.polygon

import android.graphics.Color
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.ESRIPoly
import com.bbn.openmap.layer.shape.ESRIPolygonRecord
import com.bbn.openmap.layer.shape.ESRIRecord
import com.bbn.openmap.layer.shape.ShapeFile
import com.diplom.map.layers.GEOLayer
import com.diplom.map.layers.utils.DbfReader
import com.diplom.map.layers.utils.LayerUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream


class MultiPolygonLayer(filename: String, path: String) :
    GEOLayer<MultiPolygonLayer> {

    private var polygons: ArrayList<Polygon> = ArrayList()
    private var multiPolygons = ArrayList<ShapeMultiPolygon>()
    private var pPoints = ArrayList<MyPoint>()
    private var textOverlays = ArrayList<GroundOverlay>()

    init {
        readShpFile(File("$path$filename.shp"))
        readDbfFile(File("$path$filename.dbf"))
    }

    private fun readShpFile(file: File) {
        var polygonCount = 0
        try {
            val shapeFile = ShapeFile(file)
            var record: ESRIRecord? = shapeFile.nextRecord
            while (record != null) {
                if (ShapeUtils.getStringForType(record.shapeType) == "POLYGON") {
                    val polygonRecord = record as ESRIPolygonRecord
                    val multiPolygon = ShapeMultiPolygon()
                    for (i in polygonRecord.polygons.indices) {
                        val polygon = polygonRecord.polygons[i] as ESRIPoly.ESRIFloatPoly
                        val shapePolygon = ShapePolygon()
                        for (j in 0 until polygon.nPoints) {
                            shapePolygon.addPoint(LatLng(polygon.getY(j), polygon.getX(j)))
                        }
                        pPoints.add(MyPoint(polygonCount, shapePolygon.points))
                        multiPolygon.polygons.add(shapePolygon)
                        polygonCount++
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
    }

    override fun updateVisibility(bounds: LatLngBounds) {
        updatePolygonVisibility(bounds)
        updateTextVisibility(bounds)
    }

    private fun updatePolygonVisibility(bounds: LatLngBounds) {
        var start = 0
        var end = 0

        Observable.fromIterable(pPoints)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .doOnNext {
                var isVisible = false
                for (point in 0 until it.points.size) {
                    if (bounds.contains(it.points[point])) {
                        isVisible = true
                        break
                    }
                }
                Observable.just(Pair(it.index, isVisible))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .doOnNext { pair ->
                        if (polygons[pair.arg1].isVisible != pair.arg2) {
                            polygons[pair.arg1].isVisible = pair.arg2
                        }
                    }
                    .subscribe()
            }
            .subscribe()
//        Observable.just(pPoints)
//            .subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.newThread())
//            .doOnNext {
//                while (end != it.size) {
//                    end += 100
//                    if (end > it.size)
//                        end = it.size
//                    Observable.fromIterable(it.subList(start, end))
//                        .observeOn(Schedulers.newThread())
//                        .subscribeOn(Schedulers.newThread())
//                        .doOnNext { points ->
//                            var visible = false
//                            for (point in 0 until points.points.size) {
//                                if (bounds.contains(points.points[point])) {
//                                    visible = true
//                                    break
//                                }
//                            }
//                            Observable.just(Pair(points.index, visible))
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribeOn(Schedulers.newThread())
//                                .doOnNext { pair ->
//                                    if (polygons[pair.arg1].isVisible != pair.arg2) {
//                                        polygons[pair.arg1].isVisible = pair.arg2
//                                    }
//                                }
//                                .subscribe()
//                        }
//                        .subscribe()
//                    start = end
//                }
//            }
//            .subscribe()
    }

    private fun updateTextVisibility(bounds: LatLngBounds) {

    }

    override fun getLayout(map: GoogleMap): MultiPolygonLayer {
        draw(map)
        drawText(map)
        return this
    }

    private fun drawText(map: GoogleMap) {
        for (multiPolygon in multiPolygons) {
            val builder = LatLngBounds.builder()
            for (polygon in multiPolygon.polygons)
                for (point in polygon.points) {
                    builder.include(point)
                }
            val textPosition = builder.build().center
            val text = "${multiPolygon.attributeSet[2]}: ${multiPolygon.attributeSet[1]}"
            textOverlays.add(
                LayerUtils.drawText(
                    map,
                    text,
                    textPosition,
                    120f,
                    Color.YELLOW,
                    30f
                )
            )
            map.moveCamera(CameraUpdateFactory.newLatLng(textPosition))
        }

    }

    private fun draw(map: GoogleMap) {
//        Observable.fromIterable(multiPolygons)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.newThread())
//            .doOnNext {
//                for (polygon in it.polygons) {
//                    polygons.add(
//                        map.addPolygon(
//                            PolygonOptions()
//                                .addAll(polygon.points)
//                                .strokeColor(Color.argb(150, 20, 240, 40))
//                                .fillColor(Color.argb(150, 140, 20, 140))
//                                .strokeWidth(4.0f)
//                                .visible(false)
//                                .zIndex(0f)
//                        )
//                    )
//                }
//            }
//            .subscribe()
        for (multiPolygon in multiPolygons) {
            for (polygon in multiPolygon.polygons) {
                polygons.add(
                    map.addPolygon(
                        PolygonOptions()
                            .addAll(polygon.points)
                            .strokeColor(Color.argb(150, 20, 240, 40))
                            .fillColor(Color.argb(150, 140, 20, 140))
                            .strokeWidth(4.0f)
                            .visible(false)
                            .zIndex(0f)
                    )
                )
            }
        }
    }
}

data class MyPoint(val index: Int, val points: ArrayList<LatLng>)

data class Pair<T1, T2>(val arg1: T1, val arg2: T2)