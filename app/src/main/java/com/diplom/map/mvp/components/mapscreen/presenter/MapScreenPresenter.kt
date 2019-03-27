package com.diplom.map.mvp.components.mapscreen.presenter

import com.diplom.map.layers.GEOLayer
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.diplom.map.room.AppDatabase
import com.google.android.gms.maps.GoogleMap
import java.util.*
import javax.inject.Inject

class MapScreenPresenter : BasePresenter<MapScreenContract.View>(), MapScreenContract.Presenter {

    init {
        App.get().dbInjector.inject(this)
    }

    @Inject
    lateinit var db: AppDatabase

    override fun getLayout(map: GoogleMap): ArrayList<GEOLayer<*>> {
        return arrayListOf(
            //  MultiPolygonLayer("kvartal_zone", "/storage/emulated/0/Map/", db).getLayout(map)

        )
    }
}