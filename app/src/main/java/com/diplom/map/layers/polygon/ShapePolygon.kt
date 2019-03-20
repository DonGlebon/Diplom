package com.diplom.map.layers.polygon

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class ShapePolygon {

    var points = ArrayList<LatLng>()
    lateinit var bounds: LatLngBounds


    fun addPoint(point: LatLng) {
        points.add(point)
    }

    private fun setBounds() {
        val builder = LatLngBounds.Builder()
        for (point in points) {
            builder.include(point)
        }
        bounds = builder.build()
    }

    fun build(): ShapePolygon {
        setBounds()
        return this
    }

    fun getPolygonBounds(): LatLngBounds {
        return bounds
    }
}