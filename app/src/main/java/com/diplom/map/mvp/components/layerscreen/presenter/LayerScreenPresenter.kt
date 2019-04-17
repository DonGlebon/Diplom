package com.diplom.map.mvp.components.layerscreen.presenter

import android.os.Environment
import android.util.Log
import com.diplom.map.esri.utils.ShapeFileUtils
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layerscreen.contract.LayerScreenContract
import com.diplom.map.room.AppDatabase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject


class LayerScreenPresenter : BasePresenter<LayerScreenContract.View>(), LayerScreenContract.Presenter {

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var disposable: CompositeDisposable

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
            .doOnNext { view!!.addLayerToRecycler(it) }
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
        db.globalDao().insertShapeFileData(filename, filepath, ShapeFileUtils.readShapeFile(filename, filepath))
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

//    private fun readShapeFile(name: String, mPath: String): ESRILayer {
//        val featureList = ArrayList<ESRIFeature>()
//        var type = ""
//        try {
//            val shapeFile = ShapeFile(File("${mPath + name}.shp"))
//            var record: ESRIRecord? = shapeFile.nextRecord
//            type = ShapeUtils.getStringForType(record!!.shapeType)
//            while (record != null) {
//                featureList.add(
//                    when (ShapeUtils.getStringForType(record.shapeType)) {
//                        "POLYGON", "POLYLINE" -> {
//                            parseMultiPolygon(record as ESRIPolygonRecord)
//                        }
//                        "POINT" -> {
//                            parsePoint(record as ESRIPointRecord)
//                        }
//                        else -> {
//                            ESRIFeature()
//                        }
//                    }
//                )
//                record = shapeFile.nextRecord
//            }
//        } catch (e: Exception) {
//            Log.d("Hello", "read err ${e.message}")
//        }
//        return ESRILayer(type, featureList)
//    }
//
//    private fun parseMultiPolygon(record: ESRIPolygonRecord): ESRIFeature {
//        val feature = ESRIFeature()
//        for (i in record.polygons.indices) {
//            val polygonRecord = record.polygons[i] as ESRIPoly.ESRIFloatPoly
//            val polygon =
//                ESRISubFeature()
//            for (j in 0 until polygonRecord.nPoints) {
//                polygon.addPoint(LatLng(polygonRecord.getY(j), polygonRecord.getX(j)))
//            }
//            feature.addFeature(polygon)
//        }
//        return feature
//    }
//
//    private fun parsePoint(record: ESRIPointRecord): ESRIFeature {
//        val future = ESRIFeature()
//        future.addFeature(
//            ESRISubFeature().also { it.addPoint(LatLng(record.x, record.y)) }
//        )
//        return future
//    }

}

