package com.diplom.map.mvp.components.mapscreen.presenter

import android.util.Log
import com.diplom.map.layers.GEOLayer
import com.diplom.map.layers.point.PointLayer
import com.diplom.map.layers.polygon.MultiPolygonLayer
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.diplom.map.mvp.config.room.AppDatabase
import com.diplom.map.mvp.config.room.MultiPolygon
import com.google.android.gms.maps.GoogleMap
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class MapScreenPresenter : BasePresenter<MapScreenContract.View>(), MapScreenContract.Presenter {

    init {
        App.get().dbInjector.inject(this)
    }

    @Inject
    lateinit var db: AppDatabase
    private var layout: MultiPolygonLayer =
        MultiPolygonLayer("kvartal_zone", "/storage/emulated/0/Map/")
    private var layoutP: PointLayer =
        PointLayer("airports1", "/storage/emulated/0/Map/")
    private val lay = MultiPolygonLayer("vydel", "/storage/emulated/0/Map/")

    override fun getLayout(map: GoogleMap): ArrayList<GEOLayer<*>> {
//        CompositeDisposable().add(
//            Single.just(db)
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe({
//                    //     it.multiPolygonDao().insert(MultiPolygon(4))
//                    for (i in it.multiPolygonDao().getAllMultiPolygons())
//                        Log.d("Hello", "room: ${i.uid}")
//                }, { error -> Log.d("Hello", "error") })
//        )
        return arrayListOf(layout.getLayout(map))
    }
}