package com.diplom.map.layers.polygon

import com.google.android.gms.maps.model.LatLng

class ShapePolygonBuilder {

    private val polygon = ShapePolygon()

    fun include(point: LatLng) {
        polygon.addPoint(point)
    }

    fun build(): ShapePolygon {
        return polygon.build()
    }

}