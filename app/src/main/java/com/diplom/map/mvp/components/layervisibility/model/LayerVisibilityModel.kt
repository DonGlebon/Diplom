package com.diplom.map.mvp.components.layervisibility.model

import com.diplom.map.mvp.App
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.data.LayerVisibility
import com.diplom.map.utils.ShapeFileUtils
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class LayerVisibilityModel {

    @Inject
    lateinit var db: AppDatabase

    init {
        App.get().injector.inject(this)
    }

    private fun insertLayer(filename: String, filepath: String): Boolean {
        db.globalDao()
            .insertShapeFileData(filename, filepath, ShapeFileUtils.readShapeFile(filename, filepath))
        return true
    }

    fun addLayer(filename: String, filepath: String): Single<Boolean> {
        return Single.defer {
            Single.just(insertLayer(filename, filepath))
        }
    }

    fun getLayers(): Observable<List<LayerVisibility>> {
        return db.layerDao().getLayers()
    }
}