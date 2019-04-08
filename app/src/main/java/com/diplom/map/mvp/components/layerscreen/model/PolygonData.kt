package com.diplom.map.mvp.components.layerscreen.model

import com.google.android.gms.maps.model.LatLng

class PolygonData {
    val points = ArrayList<LatLng>()
    fun addPoint(point: LatLng) {
        points.add(point)
    }
}