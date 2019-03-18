package com.diplom.map.layers.polygon

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class ShapePolygon {

    var points = ArrayList<LatLng>()
    var bounds: LatLngBounds? = null

    private fun getPolygonBounds(): LatLngBounds {
        return if (bounds == null) {
            val builder = LatLngBounds.Builder()
            for (point in points) {
                builder.include(point)
            }
            builder.build()
        } else bounds!!
    }
}