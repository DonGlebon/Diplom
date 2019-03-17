package com.diplom.map.mvp.components.mapscreen.presenter

import android.util.Log
import com.diplom.map.GeoPolyLayer
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.google.android.gms.maps.GoogleMap

class MapScreenPresenter : BasePresenter<MapScreenContract.View>(), MapScreenContract.Presenter {


    private var layout: GeoPolyLayer? = null

    init {
        Log.d("Hello", "Hello init")
    }

    override fun getLayout(map: GoogleMap): GeoPolyLayer {
        Log.d("Hello", "Hello get")
        return if (layout == null) {
            layout = GeoPolyLayer(map, "alaska", "/storage/emulated/0/Map/")
            layout as GeoPolyLayer
        } else
            layout as GeoPolyLayer
    }
}