package com.diplom.map.layers.polygon

class ShapeMultiPolygon {
    var attributeSet = ArrayList<Any>()
    var polygons = ArrayList<ShapePolygon>()
    val holes = ArrayList<ShapePolygon>()

    companion object {
        fun Builder(): ShapeMultiPolygonBuilder {
            return ShapeMultiPolygonBuilder()
        }
    }
}