package com.diplom.map.mvp.components.fragments.map.presenter

import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.fragments.map.contract.MapFragmentContract
import com.diplom.map.room.AppDatabase
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class MapFragmentPresenter : BasePresenter<MapFragmentContract.View>(), MapFragmentContract.Presenter {

    @Inject
    lateinit var db: AppDatabase

    init {
        App.get().injector.inject(this)
    }

    var lastPosition = LatLng(50.0, 50.0)
    var lastZoom = 20f

    fun setLastPlace(position: LatLng, zoom: Float) {
        lastPosition = position
        lastZoom = zoom
    }

}