package com.diplom.map.layers.polygon

import android.graphics.Color
import android.util.Log
import com.diplom.map.layers.GEOLayer
import com.diplom.map.layers.utils.LayerUtils
import com.diplom.map.room.AppDatabase
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File


class MultiPolygonLayer(
    filename: String,

    private val db: AppDatabase
) :
    GEOLayer<MultiPolygonLayer> {

    private var polygons: ArrayList<Polygon> = ArrayList()
    var multiPolygons = ArrayList<ShapeMultiPolygon>()
    private var polygonPoints = ArrayList<MyPoint>()
    private var textOverlays = ArrayList<GroundOverlay>()
    private val dbDisposable = CompositeDisposable()


    init {
        dbDisposable.add(
            db.layerDao().findLayerByName(filename)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ Log.d("Hello", "Suc $it") },
                    { Log.d("Hello", "Err ${it.message}") },
                    { Log.d("Hello", "Comp") })
        )
//        dbDisposable.add(
//            db.layerDao().getLayers().observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    Log.d("Hello", "Count: ${it.size}")
//                }
        //    )
        //  readDbfFile(File("$path$filename.dbf"))
    }


    private fun readShpFile(file: File) {
//        Log.d("Hello", "REad")


//        val shapeFile = ShapeFile(file)
//        var record: ESRIRecord? = shapeFile.nextRecord
//        var mid = 0
//        while (record != null) {
//
//            val multiPolygon = MultiPolygonData(0, layerId)
//            if (ShapeUtils.getStringForType(record.shapeType) == "POLYGON") {
//                val polygonRecord = record as ESRIPolygonRecord
//                for (i in polygonRecord.polygons.indices) {
//
//                    val polygon = polygonRecord.polygons[i] as ESRIPoly.ESRIFloatPoly
//                    for (j in 0 until polygon.nPoints) {
//                        shapePolygon.addPoint(LatLng(polygon.getY(j), polygon.getX(j)))
//                    }
//                }
//            }
//            dbDisposable.add(
//                db.multiPolygonDao().insert(multiPolygon)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe()
//            )
//            mid++
//            record = shapeFile.nextRecord

        //     }

    }

//    fun addMultiPolygon(uid: Int): Completable {
//        val multiPolygon = MultiPolygonData(uid)
//        return db.multiPolygonDao().insert(multiPolygon)
//    }

    //fun addPolygon(mpid: Int)

//    private fun readShpFile(file: File) {
//        var polygonCount = 0
//        var roomMultiId = 0
//        try {
//            val shapeFile = ShapeFile(file)
//            var record: ESRIRecord? = shapeFile.nextRecord
//            while (record != null) {
//                if (ShapeUtils.getStringForType(record.shapeType) == "POLYGON") {
// val polygonRecord = record as ESRIPolygonRecord
//                    val multiPolygon = ShapeMultiPolygon()
//                    val roomMultyPolygon = MultiPolygonData(roomMultiId)
//                    for (i in polygonRecord.polygons.indices) {
//                        val polygon = polygonRecord.polygons[i] as ESRIPoly.ESRIFloatPoly
//                        val shapePolygon = ShapePolygon()
//                        for (j in 0 until polygon.nPoints) {
//                            shapePolygon.addPoint(LatLng(polygon.getY(j), polygon.getX(j)))
//                        }
//                        polygonPoints.add(MyPoint(polygonCount, shapePolygon.points))
//                        multiPolygon.polygons.add(shapePolygon)
//                        polygonCount++
//                    }
//                    multiPolygons.add(multiPolygon)
//                    roomMultiId++
//                }
//                record = shapeFile.nextRecord
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

//    private fun readDbfFile(file: File) {
////        val dbfStream = DbfReader(FileInputStream(file))
////        for (i in 0 until dbfStream.records.size)
////            multiPolygons[i].attributeSet = dbfStream.records[i] as ArrayList<Any>
//    }

    override fun updateVisibility(bounds: LatLngBounds, zoom: Float) {
        updatePolygonVisibility(bounds)
    }

    private fun hideText() {
        for (text in textOverlays)
            text.isVisible = false
    }

    private fun hidePolygon() {
        for (polygon in polygons)
            polygon.isVisible = false
    }

    private fun updatePolygonVisibility(bounds: LatLngBounds) {
        Observable.fromIterable(polygonPoints)
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
//        Observable.just(polygonPoints)
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
        for (text in textOverlays)
            text.isVisible = bounds.contains(text.position)

    }

    override fun getLayout(map: GoogleMap): MultiPolygonLayer {
//        draw(map)
        //   drawText(map)
        return this
    }

    private fun drawText(map: GoogleMap) {
        for (multiPolygon in multiPolygons) {
            val builder = LatLngBounds.builder()
            for (polygon in multiPolygon.polygons)
                for (point in polygon.points) {
                    builder.include(point)
                }
            textOverlays.add(
                LayerUtils.drawText(
                    map,
                    "${multiPolygon.attributeSet[2]}: ${multiPolygon.attributeSet[1]}",
                    builder.build().center,
                    120f,
                    Color.YELLOW,
                    30f
                )
            )
        }

    }

    private fun draw(map: GoogleMap) {
        val s = System.currentTimeMillis()
        for (multiPolygon in multiPolygons) {
            for (polygon in multiPolygon.polygons) {
                polygons.add(
                    map.addPolygon(
                        PolygonOptions()
                            .addAll(polygon.points)
                            .strokeColor(Color.argb(150, 20, 240, 40))
                            .fillColor(Color.argb(150, 140, 20, 140))
                            .strokeWidth(4.0f)
                            //   .visible(false)
                            .zIndex(0f)
                    )
                )
            }
        }
        Log.d("Hello", "draw complete in ${System.currentTimeMillis() - s}")
    }
}

data class MyPoint(val index: Int, val points: ArrayList<LatLng>)

data class Pair<T1, T2>(val arg1: T1, val arg2: T2)