package com.diplom.map.mvp.components.mapscreen.presenter

import android.util.Log
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.diplom.map.esri.model.ESRITileProvider
import com.diplom.map.mvp.components.fragments.map.model.MultiPolygonTileProvider
import com.diplom.map.mvp.components.fragments.map.model.PolyLineTileProvider
import com.diplom.map.room.AppDatabase
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MapScreenPresenter : BasePresenter<MapScreenContract.View>(), MapScreenContract.Presenter {


    @Inject
    lateinit var db: AppDatabase

    private val disposable = CompositeDisposable()

    init {
        App.get().injector.inject(this)
    }

    fun mapReady() {
        disposable.add(
            db.layerDao().getDataLayers()
                .subscribeOn(Schedulers.io())
                .flatMap {
                    var p= 0
                    Observable.fromIterable(it)
                }
                .map {
                    val provider: ESRITileProvider = if (it.GeometryType == "POLYGON")
                        MultiPolygonTileProvider()
                    else
                        PolyLineTileProvider()
                    provider.addLayer(it)
                    provider
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { view!!.addTileProvider(it) }
                .doOnError { Log.d("Hello", "MapReady error: ${it.message}") }
                .subscribe()
        )
    }
}

