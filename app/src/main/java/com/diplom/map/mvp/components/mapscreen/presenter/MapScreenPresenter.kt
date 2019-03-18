package com.diplom.map.mvp.components.mapscreen.presenter

import com.diplom.map.layers.GEOLayer
import com.diplom.map.layers.point.PointLayer
import com.diplom.map.layers.polygon.MultiPolygonLayer
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.google.android.gms.maps.GoogleMap

class MapScreenPresenter : BasePresenter<MapScreenContract.View>(), MapScreenContract.Presenter {

    private var layout: MultiPolygonLayer =
        MultiPolygonLayer("kvartal_zone", "/storage/emulated/0/Map/")
    private var layoutP: PointLayer =
        PointLayer("airports1", "/storage/emulated/0/Map/")

    override fun getLayout(map: GoogleMap): ArrayList<GEOLayer<*>> {
        return arrayListOf(layout.getLayout(map), layoutP.getLayout(map))
    }
}