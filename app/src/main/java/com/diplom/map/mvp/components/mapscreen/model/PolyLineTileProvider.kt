package com.diplom.map.mvp.components.mapscreen.model

import android.graphics.*
import com.diplom.map.room.data.LayerData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.projection.SphericalMercatorProjection
import java.io.ByteArrayOutputStream

class PolyLineTileProvider : TileProvider {

    private val mTileSize = 512
    private val mProjection = SphericalMercatorProjection(mTileSize.toDouble())
    private val mScale = 2
    private var mDimension = mScale * mTileSize
    private var mPath = Path()
    private var oldZoom = -1
    private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.rgb(20, 250, 150)
        strokeWidth = .0000325f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }


    override fun getTile(x: Int, y: Int, zoom: Int): Tile {
        val scale = Math.pow(2.0, zoom.toDouble()).toFloat() * mScale
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postTranslate((-x * mDimension).toFloat(), (-y * mDimension).toFloat())
        }
        val bitmap =
            Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply {
            this.matrix = matrix
        }
        if (zoom != oldZoom)
            updateStrokeWidth(zoom)
        else
            oldZoom = zoom
        canvas.drawPath(mPath, paintStroke)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return Tile(mDimension, mDimension, baos.toByteArray())
    }

    private fun updateStrokeWidth(zoom: Int) {
        val strokeWidth = .0000002f * Math.pow(2.0, (23 - zoom).toDouble()).toFloat()
        paintStroke.strokeWidth = if (strokeWidth < .000035f) .000035f else strokeWidth
    }

    private fun getPath(multiPolygons: ArrayList<ProjectionMultiPolygon>): Path {
        val path = Path()
        for (multiPolygon in multiPolygons) {
            for (polygon in multiPolygon.polygons) {
                val polygonPath = Path()
                val points = polygon.points
                polygonPath.moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    polygonPath.lineTo(points[i].x, points[i].y)
                }
                path.addPath(polygonPath)
            }
        }
        return path
    }

    fun addLayer(layerData: LayerData) {
        val projectionData = layerDataToProjectionData(layerData)
        mPath = getPath(projectionData)
    }

    private fun layerDataToProjectionData(layerData: LayerData): ArrayList<ProjectionMultiPolygon> {
        val mPolygons = ArrayList<ProjectionMultiPolygon>()
        for (multiPolygon in layerData.multiPolygons) {
            val pmp = ProjectionMultiPolygon()
            for (polygon in multiPolygon.polygons) {
                val pp = ProjectionPolygon(polygon.uid)
                for (point in polygon.points.sortedBy { it.uid }) {
                    val localPoint = mProjection.toPoint(LatLng(point.Lat, point.Lng))
                    pp.add(ProjectionPoint(point.uid, localPoint.x.toFloat(), localPoint.y.toFloat()))
                }
                pmp.add(pp)
            }
            mPolygons.add(pmp)
        }
        return mPolygons
    }

    private class ProjectionMultiPolygon {
        var polygons = ArrayList<ProjectionPolygon>()
        fun add(polygon: ProjectionPolygon) {
            polygons.add(polygon)
        }
    }

    private class ProjectionPolygon(var uid: Long) {
        var points = ArrayList<ProjectionPoint>()
        fun add(point: ProjectionPoint) {
            points.add(point)
        }
    }

    private data class ProjectionPoint(
        val uid: Long,
        val x: Float,
        val y: Float
    )
}