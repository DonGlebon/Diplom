package com.diplom.map.mvp.components.map.presenter

import android.util.Log
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.map.contract.MapFragmentContract
import com.diplom.map.mvp.components.map.model.LayersTileProvider
import com.diplom.map.room.AppDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MapFragmentPresenter : BasePresenter<MapFragmentContract.View>(), MapFragmentContract.Presenter {

    @Inject
    lateinit var db: AppDatabase

    val disposable = CompositeDisposable()

    init {
        App.get().injector.inject(this)
    }

    fun mapReady() {
        disposable.add(
            db.layerDao().getDataLayers()
                .subscribeOn(Schedulers.io())
                .map {
                    val provider = LayersTileProvider()
                    for (layer in it)
                        provider.addLayer(layer)
                    provider
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { view!!.addTileProvider(it) }
                .doOnError { Log.d("Hello", "MapReady error: ${it.message}") }
                .doOnComplete { }
                .subscribe()
        )
    }

}