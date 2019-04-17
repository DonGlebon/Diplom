package com.diplom.map.esri.model

import com.google.android.gms.maps.model.LatLng

class ESRISubFeature {
    val points = ArrayList<LatLng>()
    fun addPoint(point: LatLng) {
        points.add(point)
    }
}