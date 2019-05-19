package com.diplom.map.mvp.components.map.presenter

import android.util.Log
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.map.contract.MapFragmentContract
import com.diplom.map.mvp.components.map.model.LayersTileProvider
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.data.FDData
import com.diplom.map.room.data.SubFeatureData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MapFragmentPresenter : BasePresenter<MapFragmentContract.View>(), MapFragmentContract.Presenter {
    override fun getFeatureById(uid: Long): Single<List<SubFeatureData>> {
        return db.layerDao().getSubFeatures(uid)
    }

    override fun getLayerDataById(uid: Long): Single<List<FDData>> {
        return db.layerDao().getFeatureData(uid)
    }

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
                .map {
                    Log.d("Hello", "MapReady1 maap:")
                    val provider = LayersTileProvider()
                    for (layer in it)
                        provider.addLayer(layer)
                    provider
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    Log.d("Hello", "MapReady1 next:")
                    if (it != null)
                        view?.addTileProvider(it)
                }
                .doOnError { Log.d("Hello", "MapReady1 error: ${it.message}") }
                //.doOnComplete { }
                .subscribe()
        )
    }

}