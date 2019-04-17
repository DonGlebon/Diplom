package com.diplom.map.mvp.components.map.model

import android.graphics.*
import android.util.Log
import com.diplom.map.esri.model.ESRITileProvider
import com.diplom.map.room.data.FeatureStyleData
import com.diplom.map.room.data.LayerData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Tile
import com.google.maps.android.projection.SphericalMercatorProjection
import java.io.ByteArrayOutputStream

class LayersTileProvider : ESRITileProvider() {

    private val mTileSize = 512
    private var mScale = 1f
    private val mProjection = SphericalMercatorProjection(mTileSize.toDouble())
    private val mDimension = (mScale * mTileSize).toInt()

    private var polygonsBounds = ArrayList<PolygonBounds>()

    private var sv = 1f
    private var oldZoom = -1
    private var baseStrokeWidth = .000035f * ((mTileSize / 512) * mScale)

    private var mPath = ArrayList<FeatureStylePath>()

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
        for (path in mPath.sortedWith(compareBy { it.zIndex })
            .filter { zoom <= it.maxZoom && zoom >= it.minZoom }) {
            path.draw(canvas, sv)
        }
        Log.d("Hello","Zoom: $zoom")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return Tile(mDimension, mDimension, baos.toByteArray())
    }

    private fun updateStrokeWidth(zoom: Int) {
        val strokeWidth = .00000012f * Math.pow(2.0, (23 - zoom).toDouble()).toFloat()
        sv = if (strokeWidth < baseStrokeWidth) baseStrokeWidth else strokeWidth
    }


    override fun addLayer(layerData: LayerData) {
        LDT(layerData)
    }

    private fun LDT(layerData: LayerData) {
        for (feature in layerData.featureList) {
            val stylePath = FeatureStylePath(layerData.GeometryType, feature.style[0]).apply {
                zIndex = layerData.ZIndex
                maxZoom = layerData.maxZoom
                minZoom = layerData.minZoom
            }
            for (subFeature in feature.subFeatures) {
                val path = Path()
                val pointList = subFeature.points.sortedBy { it.uid }
                val startPoint = mProjection.toPoint(LatLng(pointList[0].Lat, pointList[0].Lng))
                path.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
                for (i in 1 until pointList.size) {
                    val localPoint = mProjection.toPoint(LatLng(pointList[i].Lat, pointList[i].Lng))
                    path.lineTo(localPoint.x.toFloat(), localPoint.y.toFloat())
                }
                if (layerData.GeometryType == "POLYGON")
                    path.close()
                stylePath.addPath(path)
            }
            mPath.add(stylePath)
        }
    }

    fun getPolygonByClick(position: LatLng) {
        val point = mProjection.toPoint(position)
        for (polygonBounds in polygonsBounds)
            if (polygonBounds.bounds.contains(point.x.toFloat(), point.y.toFloat()))
                Log.d("Hello", "SubFeature: ${polygonBounds.uid}")
    }


    class FeatureStylePath(val type: String, style: FeatureStyleData) {

        var zIndex = 0
        var maxZoom = 2
        var minZoom = 22

        var mPath = Path()

        var paintFill: Paint? = null
        var paintStroke: Paint? = null

        var strokeWidthModifier = 1f

        fun addPath(path: Path) {
            mPath.addPath(path)
        }


        fun draw(canvas: Canvas, sv: Float) {
            if (type == "POLYGON") {
                canvas.drawPath(mPath, paintFill!!)
            }
            paintStroke?.strokeWidth = sv * strokeWidthModifier
            canvas.drawPath(mPath, paintStroke!!)
        }

        init {
            strokeWidthModifier = style.strokeWidth
            if (type == "POLYGON") {
                paintFill = Paint().apply {
                    this.style = Paint.Style.FILL
                    color = style.fillColor
                }
            }
            paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
                color = style.strokeColor
                strokeWidth = style.strokeWidth
            }
        }
    }
}