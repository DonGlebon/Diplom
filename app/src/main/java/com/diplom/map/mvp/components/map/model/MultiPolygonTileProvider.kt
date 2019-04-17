package com.diplom.map.mvp.components.map.model

import android.graphics.*
import android.util.Log
import com.diplom.map.esri.model.ESRITileProvider
import com.diplom.map.room.data.LayerData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Tile
import com.google.maps.android.projection.SphericalMercatorProjection
import java.io.ByteArrayOutputStream

class MultiPolygonTileProvider : ESRITileProvider() {

    private val mTileSize = 512
    private val mProjection = SphericalMercatorProjection(mTileSize.toDouble())
    private var mScale = 1f
    private val mDimension = (mScale * mTileSize).toInt()
    private var polygonsBounds = ArrayList<PolygonBounds>()
    private var oldZoom = -1
    private var baseStrokeWidth = .000035f * ((mTileSize / 512) * mScale)
    private val paintFill = Paint().apply {
        style = Paint.Style.FILL
        color = Color.argb(150, 100, 20, 200)
    }
    private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.rgb(40, 250, 30)
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private var mPath = Path()

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
        canvas.drawPath(mPath, paintFill)
        canvas.drawPath(mPath, paintStroke)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return Tile(mDimension, mDimension, baos.toByteArray())
    }

    private fun updateStrokeWidth(zoom: Int) {
        val strokeWidth = .0000002f * Math.pow(2.0, (23 - zoom).toDouble()).toFloat()
        paintStroke.strokeWidth = if (strokeWidth < baseStrokeWidth) baseStrokeWidth else strokeWidth
    }

    private fun getPath(multiPolygons: ArrayList<ProjectionMultiPolygon>): Path {
        val path = Path()
        for (multiPolygon in multiPolygons) {
            for (polygon in multiPolygon.polygons) {
                val polygonPath = Path()
                polygonPath.incReserve(polygon.points.size)
                val points = polygon.points
                polygonPath.moveTo(points[0].x, points[0].y)
                for (i in 0 until points.size) {
                    polygonPath.lineTo(points[i].x, points[i].y)
                }
                polygonPath.close()
                val bounds = RectF()
                polygonPath.computeBounds(bounds, false)
                polygonsBounds.add(
                    PolygonBounds(
                        polygon.uid,
                        bounds
                    )
                )
                path.addPath(polygonPath)
            }
        }
        return path
    }

    override fun addLayer(layerData: LayerData) {
        val projectionData = layerDataToProjectionData(layerData)
        mPath = getPath(projectionData)
    }

    private fun layerDataToProjectionData(layerData: LayerData): ArrayList<ProjectionMultiPolygon> {
        val mPolygons = ArrayList<ProjectionMultiPolygon>()
        for (multiPolygon in layerData.featureList) {
            val pmp =
                ProjectionMultiPolygon()
            for (polygon in multiPolygon.subFeatures) {
                val pp =
                    ProjectionPolygon(polygon.uid)
                for (point in polygon.points.sortedBy { it.uid }) {
                    val localPoint = mProjection.toPoint(LatLng(point.Lat, point.Lng))
                    pp.add(
                        ProjectionPoint(
                            point.uid,
                            localPoint.x.toFloat(),
                            localPoint.y.toFloat()
                        )
                    )
                }
                pmp.add(pp)
            }
            mPolygons.add(pmp)
        }
        return mPolygons
    }

    fun getPolygonByClick(position: LatLng) {
        val point = mProjection.toPoint(position)
        for (polygonBounds in polygonsBounds)
            if (polygonBounds.bounds.contains(point.x.toFloat(), point.y.toFloat()))
                Log.d("Hello", "SubFeature: ${polygonBounds.uid}")
    }

}