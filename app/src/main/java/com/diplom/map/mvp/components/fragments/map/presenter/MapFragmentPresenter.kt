package com.diplom.map.mvp.components.fragments.map.presenter

import android.util.Log
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.fragments.map.contract.MapFragmentContract
import com.diplom.map.mvp.components.fragments.map.model.LayersTileProvider
import com.diplom.map.room.AppDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MapFragmentPresenter : BasePresenter<MapFragmentContract.View>(), MapFragmentContract.Presenter {

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var disposable: CompositeDisposable

    init {
        App.get().injector.inject(this)
    }

    fun mapReady() {
      //  val provider = LayersTileProvider()
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
                .doOnNext {view!!.addTileProvider(it) }
                .doOnError { Log.d("Hello", "MapReady error: ${it.message}") }
                .doOnComplete { }
                .subscribe()
        )
    }

}