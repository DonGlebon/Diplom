package com.diplom.map.mvp.components.layerscreen.model

class MultiPolygonData {
    val polygons = ArrayList<PolygonData>()
    fun addPolygon(polygon: PolygonData) {
        polygons.add(polygon)
    }

}