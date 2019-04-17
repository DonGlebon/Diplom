package com.diplom.map.mvp.components.fragments.map.model

class ProjectionMultiPolygon {
    var polygons = ArrayList<ProjectionPolygon>()
    fun add(polygon: ProjectionPolygon) {
        polygons.add(polygon)
    }
}