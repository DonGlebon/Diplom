package com.diplom.map.layers.polygon

class ShapeMultiPolygonBuilder {

    private val multiPolygon = ShapeMultiPolygon()

    fun include(polygon: ShapePolygon) {

    }

    fun build(): ShapeMultiPolygon {
        return multiPolygon
    }

}