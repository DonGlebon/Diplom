package com.diplom.map.mvp.components.map.model

import android.graphics.*
import com.diplom.map.esri.model.ESRITileProvider
import com.diplom.map.room.data.LayerData
import com.diplom.map.room.data.ThemeStyleValuesData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Tile
import com.google.maps.android.projection.Point
import com.google.maps.android.projection.SphericalMercatorProjection
import java.io.ByteArrayOutputStream


class LayersTileProvider : ESRITileProvider() {

    private val mTileSize = 512
    private var mScale = 1f
    private val mProjection = SphericalMercatorProjection(mTileSize.toDouble())
    private val mDimension = (mScale * mTileSize).toInt()

    private var strokeWidth = 1f
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
            path.draw(canvas, strokeWidth)
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return Tile(mDimension, mDimension, baos.toByteArray())
    }

    private fun updateStrokeWidth(zoom: Int) {
        val strokeWidth = .00000012f * Math.pow(2.0, (23 - zoom).toDouble()).toFloat()
        this.strokeWidth = if (strokeWidth < baseStrokeWidth) baseStrokeWidth else strokeWidth
    }


    override fun addLayer(layerData: LayerData) {
        addNewLayer(layerData)
    }

    private fun addNewLayer(layerData: LayerData) {
        val activeStyle = layerData.styles.find { it.uid == layerData.themeId }
        for (i in 0 until layerData.featureList.size) {
            val feature = layerData.featureList[i]
            var empty = ThemeStyleValuesData(0, "solid", 0, Color.GRAY, Color.BLACK, 1f)
            val style = if (activeStyle != null) {
                for (value in activeStyle.values)
                    if (value.value == feature.data.find { it.ColumnName == activeStyle.columnName }?.value)
                        empty = value
                empty

            } else
                empty
            val stylePath = FeatureStylePath(layerData.GeometryType, style).apply {
                uid = feature.uid
                zIndex = layerData.ZIndex
                maxZoom = layerData.maxZoom
                minZoom = layerData.minZoom
            }
            for (subFeature in feature.subFeatures) {
                val path = Path()
                val pointList = subFeature.points.sortedBy { it.uid }
                val localPoints = ArrayList<Point>()
                val startPoint = mProjection.toPoint(LatLng(pointList[0].Lat, pointList[0].Lng))
                path.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
                for (i in 1 until pointList.size) {
                    val localPoint = mProjection.toPoint(LatLng(pointList[i].Lat, pointList[i].Lng))
                    localPoints.add(localPoint)
                    path.lineTo(localPoint.x.toFloat(), localPoint.y.toFloat())
                }
                if (layerData.GeometryType == "POLYGON")
                    path.close()
                stylePath.addPath(path)
                stylePath.pointList.add(localPoints)
            }
            stylePath.generateBounds()
            mPath.add(stylePath)
        }
    }

    override fun getPolygonByClick(map: GoogleMap, position: LatLng, zoom: Float): Long {
        val localPoint = mProjection.toPoint(position)
        val containsBounds = ArrayList<FeatureStylePath>()
        for (feature in mPath
            .sortedWith(compareBy { it.zIndex })
            .asReversed()
            .filter { zoom <= it.maxZoom && zoom >= it.minZoom })
            if (feature.bounds.contains(localPoint.x.toFloat(), localPoint.y.toFloat())) {
                containsBounds.add(feature)
            }
        return when {
            containsBounds.size == 0 -> -1L
            containsBounds.size == 1 -> containsBounds[0].uid
            else -> {
                for (feature in containsBounds)
                    for (points in feature.pointList) {
                        val listX = ArrayList<Float>()
                        val listY = ArrayList<Float>()
                        for (point in points) {
                            listX.add(point.x.toFloat())
                            listY.add(point.y.toFloat())
                        }
                        if (isPointInPolygon(localPoint, points))
                            return feature.uid
                    }
                return -1L
            }

        }
    }

    private fun isPointInPolygon(p: Point, polygon: ArrayList<Point>): Boolean {
        var minX = polygon[0].x
        var maxX = polygon[0].y
        var minY = polygon[0].x
        var maxY = polygon[0].y
        for (i in 1 until polygon.size) {
            val q = polygon[i]
            minX = Math.min(q.x, minX)
            maxX = Math.max(q.x, maxX)
            minY = Math.min(q.y, minY)
            maxY = Math.max(q.y, maxY)
        }

        if (p.x < minX || p.x > maxX || p.y < minY || p.y > maxY) {
            return false
        }

        var inside = false
        var i = 0
        var j = polygon.size - 1
        while (i < polygon.size) {
            if (polygon[i].y > p.y != polygon[j].y > p.y && p.x < (polygon[j].x - polygon[i].x) * (p.y - polygon[i].y) / (polygon[j].y - polygon[i].y) + polygon[i].x) {
                inside = !inside
            }
            j = i++
        }

        return inside
    }

    class FeatureStylePath(val type: String, style: ThemeStyleValuesData) {

        var uid = -1L
        var zIndex = 0
        var maxZoom = 2
        var minZoom = 22
        var bounds = RectF()
        var mPath = Path()

        val pointList = ArrayList<ArrayList<Point>>()
        var paintFill: Paint? = null
        var paintStroke: Paint? = null
        var strokeWidthModifier = 1f

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

        fun addPath(path: Path) {
            mPath.addPath(path)
        }

        fun generateBounds() {
            mPath.computeBounds(bounds, false)
        }

        fun draw(canvas: Canvas, strokeWidth: Float) {
            if (type == "POLYGON") {
                canvas.drawPath(mPath, paintFill!!)
            }
            paintStroke?.strokeWidth = strokeWidth * strokeWidthModifier
            canvas.drawPath(mPath, paintStroke!!)
        }

    }
}