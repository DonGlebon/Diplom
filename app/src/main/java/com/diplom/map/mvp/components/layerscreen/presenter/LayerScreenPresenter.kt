package com.diplom.map.mvp.components.layerscreen.presenter

import android.util.Log
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.ESRIPoly
import com.bbn.openmap.layer.shape.ESRIPolygonRecord
import com.bbn.openmap.layer.shape.ESRIRecord
import com.bbn.openmap.layer.shape.ShapeFile
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layerscreen.contract.LayerScreenContract
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.entities.Layer
import com.diplom.map.room.entities.MultiPolygon
import com.diplom.map.room.entities.Point
import com.diplom.map.room.entities.Polygon
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

class LayerScreenPresenter : BasePresenter<LayerScreenContract.View>(), LayerScreenContract.Presenter {

    init {
        App.get().dbInjector.inject(this)
    }

    @Inject
    lateinit var db: AppDatabase

    private val disposable = CompositeDisposable()

    override fun addLayer(file: File) {
        view!!.displayProgressBar(true)
        addLayer("kvartal_zone", "/storage/emulated/0/Map/")
    }

    private fun addLayer(name: String, path: String) {
        disposable.add(db.layerDao().findLayerByName(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { addPolygons(it) },
                { Log.d("Hello", "Find layer error: ${it.message}") },
                { insertLayerToDatabase(name, path) }
            )
        )
    }

    private fun insertLayerToDatabase(name: String, path: String) {
        disposable.add(db.layerDao().insert(Layer(0, name, path))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { addPolygons(it) },
                { Log.d("Hello", "Insert err") }
            )
        )
    }

    private fun addPolygons(lid: Long) {
        disposable.add(db.layerDao().getLayerById(lid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { layer ->
                    val shapePolygons = readShapeFile(layer.name, layer.path)
                    val multiPolygons = ArrayList<MultiPolygon>()
                    for (i in 0 until shapePolygons.size)
                        multiPolygons.add(MultiPolygon(0, lid))
                    clearOldLayerData(layer.uid, multiPolygons, shapePolygons)
                },
                { Log.d("Hello", "Error 3 ${it.message}") }
            )
        )
    }

    private fun clearOldLayerData(
        lid: Long,
        multiPolygons: ArrayList<MultiPolygon>,
        shapePolygons: ArrayList<MultiPolygonData>
    ) {
        disposable.add(db.multiPolygonDao().deleteAll(lid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe
            { insertMultiPolygonsToDatabase(multiPolygons, shapePolygons) }
        )
    }

    private fun insertMultiPolygonsToDatabase(
        multiPolygons: ArrayList<MultiPolygon>,
        shapePolygons: ArrayList<MultiPolygonData>
    ) {
        disposable.add(db.multiPolygonDao().insert(multiPolygons)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { multiPolygonList ->
                    val polygons = ArrayList<Polygon>()
                    for (i in 0 until shapePolygons.size) {
                        for (j in 0 until shapePolygons[i].polygons.size)
                            polygons.add(Polygon(0, multiPolygonList[i]))
                    }
                    insertPolygonsToDatabase(polygons, shapePolygons)
                },
                { Log.d("Hello", "Error 4 ${it.message}") }
            )
        )
    }

    private fun insertPolygonsToDatabase(
        polygons: ArrayList<Polygon>,
        shapePolygons: ArrayList<MultiPolygonData>
    ) {
        disposable.add(db.polygonDao().insert(polygons)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { polygonList ->
                    val points = ArrayList<Point>()
                    for (pol in shapePolygons)
                        for (i in 0 until pol.polygons.size)
                            for (point in pol.polygons[i].points)
                                points.add(
                                    Point(
                                        0,
                                        polygonList[i],
                                        point.latitude,
                                        point.longitude
                                    )
                                )
                    insertPointsToDatabase(points)
                },
                { Log.d("Hello", "Error 5w ${it.message}") }
            )
        )
    }

    private fun insertPointsToDatabase(points: ArrayList<Point>) {
        disposable.add(db.pointDao().insert(points)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { view!!.displayProgressBar(false) },
                { Log.d("Hello", "Error 6 ${it.message}") }
            )
        )
    }

    private fun readShapeFile(name: String, path: String): ArrayList<MultiPolygonData> {
        val multiPolygons = ArrayList<MultiPolygonData>()
        try {
            val shapeFile = ShapeFile(File("${path + name}.shp"))
            var record: ESRIRecord? = shapeFile.nextRecord
            while (record != null) {
                if (ShapeUtils.getStringForType(record.shapeType) == "POLYGON") {
                    val multiPolygonRecord = record as ESRIPolygonRecord
                    val multiPolygon =
                        MultiPolygonData()
                    for (i in multiPolygonRecord.polygons.indices) {
                        val polygonRecord = multiPolygonRecord.polygons[i] as ESRIPoly.ESRIFloatPoly
                        val polygon =
                            PolygonData()
                        for (j in 0 until polygonRecord.nPoints) {
                            polygon.addPoint(LatLng(polygonRecord.getY(j), polygonRecord.getX(j)))
                        }
                        multiPolygon.addPolygon(polygon)
                    }
                    multiPolygons.add(multiPolygon)
                }
                record = shapeFile.nextRecord
            }
        } catch (e: Exception) {
            Log.d("Hello", "read err ${e.message}")
        }
        return multiPolygons
    }

    class MultiPolygonData {
        val polygons = ArrayList<PolygonData>()
        fun addPolygon(polygon: PolygonData) {
            polygons.add(polygon)
        }

    }

    class PolygonData {
        val points = ArrayList<LatLng>()
        fun addPoint(point: LatLng) {
            points.add(point)
        }
    }
}