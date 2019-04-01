package com.diplom.map.mvp.components.mapscreen.presenter

import android.graphics.*
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.data.LayerData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.projection.SphericalMercatorProjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class MapScreenPresenter : BasePresenter<MapScreenContract.View>(), MapScreenContract.Presenter {


    private val provider: LayerTileProvider

    init {
        App.get().injector.inject(this)
        provider = LayerTileProvider()
    }


    @Inject
    lateinit var db: AppDatabase

    val disposable = CompositeDisposable()

    fun getTileProvider(): LayerTileProvider {
        disposable.add(
            db.layerDao().getLayerData(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ provider.addLayer(it) }, {})
        )
        return provider
    }

    class LayerTileProvider : TileProvider {

        private val mTileSize = 512
        private val mProjection = SphericalMercatorProjection(mTileSize.toDouble())
        private val mScale = 2
        private var mDimension = mScale * mTileSize
        private var polygonsBounds = ArrayList<PolygonBounds>()

        private var mPath = Path()

        override fun getTile(x: Int, y: Int, zoom: Int): Tile {
            val matrix = Matrix()
            val scale = Math.pow(2.0, zoom.toDouble()).toFloat() * mScale
            matrix.postScale(scale, scale)
            matrix.postTranslate((-x * mDimension).toFloat(), (-y * mDimension).toFloat())
            val bitmap = Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.matrix = matrix
            val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = Color.rgb(40, 250, 30)
                strokeWidth = .000005f * (23 - zoom) * 2.16f
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            val paint = Paint().apply {
                style = Paint.Style.FILL
                color = Color.argb(150, 100, 20, 200)
            }
            canvas.drawPath(mPath, paint)
            canvas.drawPath(mPath, paintStroke)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            return Tile(mDimension, mDimension, baos.toByteArray())
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
                        PolygonBounds(polygon.uid, bounds)
                    )
                    path.addPath(polygonPath)
                }
            }
            return path
        }

        fun addLayer(layerData: LayerData) {
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
            mPath = getPath(mPolygons)

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
                for (p in points)
                    if (p.x == point.x && p.y == point.y)
                        return
                points.add(point)
            }
        }

        private data class ProjectionPoint(
            val uid: Long,
            val x: Float,
            val y: Float
        )

        private data class PolygonBounds(val uid: Long, val bounds: RectF)

    }
}