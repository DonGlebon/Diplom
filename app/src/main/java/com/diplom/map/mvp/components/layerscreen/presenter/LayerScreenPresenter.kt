package com.diplom.map.mvp.components.layerscreen.presenter

import android.os.Environment
import android.util.Log
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.ESRIPoly
import com.bbn.openmap.layer.shape.ESRIPolygonRecord
import com.bbn.openmap.layer.shape.ESRIRecord
import com.bbn.openmap.layer.shape.ShapeFile
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layerscreen.contract.LayerScreenContract
import com.diplom.map.mvp.components.layerscreen.model.MultiPolygonData
import com.diplom.map.mvp.components.layerscreen.model.PolygonData
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.entities.Layer
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject


class LayerScreenPresenter : BasePresenter<LayerScreenContract.View>(), LayerScreenContract.Presenter {

    @Inject
    lateinit var db: AppDatabase
    private val disposable = CompositeDisposable()

    init {
        App.get().injector.inject(this)
    }

    fun onRecyclerIsReady() {
        displayLayers()
    }

    private fun displayLayers() {
        disposable.add(db.layerDao().getLayers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { view!!.addLayerToRecycler(it as ArrayList<Layer>) }
            .doOnError { Log.d("Hello", "Get layers error: ${it.message}") }
            .subscribe()
        )
    }

    override fun addNewLayer(file: File) {
        view!!.displayProgressBar(true)
        val path = file.absoluteFile.absolutePath
        val filename = path.substring(path.lastIndexOf('/') + 1).substringBefore('.')
        val uriPath = path.substringBefore(':')
        val pp = path.substring(path.lastIndexOf(':') + 1, path.lastIndexOf('/'))
        val filepath = if (uriPath.toLowerCase().contains("primary"))
            "/storage/emulated/0/$pp/"
        else
            "${Environment.getExternalStorageDirectory()}/"
        addLayer(filename, filepath)
    }

    private fun insertLayer(filename: String, filepath: String): Boolean {
        db.globalDao().insertShapeFileData(filename, filepath, readShapeFile(filename, filepath))
        return true
    }


    private fun addLayer(filename: String, filepath: String) {
        disposable.add(Single.defer { Single.just(insertLayer(filename, filepath)) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { view!!.displayProgressBar(false) }
            .doOnError { Log.d("Hello", "Error: ${it.message}") }
            .subscribe())
    }

    private fun readShapeFile(name: String, path: String): ArrayList<MultiPolygonData> {
        val multiPolygons = ArrayList<MultiPolygonData>()
        try {
            val shapeFile = ShapeFile(File("${path + name}.shp"))
            var record: ESRIRecord? = shapeFile.nextRecord
            val s = ShapeUtils.getStringForType(record!!.shapeType)
            while (record != null) {
                if (ShapeUtils.getStringForType(record.shapeType) == "POLYLINE" || ShapeUtils.getStringForType(record.shapeType) == "POLYGON") {
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

}

