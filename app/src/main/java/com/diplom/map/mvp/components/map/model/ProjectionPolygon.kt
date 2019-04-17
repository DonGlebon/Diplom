package com.diplom.map.mvp.components.map.model

class ProjectionPolygon(var uid: Long) {
    var points = ArrayList<ProjectionPoint>()
    fun add(point: ProjectionPoint) {
        points.add(point)
    }
}