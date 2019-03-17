package com.diplom.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.ESRIPointRecord
import com.bbn.openmap.layer.shape.ESRIRecord
import com.bbn.openmap.layer.shape.ShapeFile
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.io.File
import java.io.FileInputStream

class PointLayer(filename: String, path: String) : GEOLayer<PointLayer> {

    private var circles: ArrayList<Circle> = ArrayList()
    private var textOverlays: ArrayList<GroundOverlay> = ArrayList()

    private var _esirPoints: ArrayList<ESIRPoint> = ArrayList()

    init {
        readShpFile(File("$path$filename.shp"))
        readDbfFile(File("$path$filename.dbf"))
    }

    private fun readShpFile(file: File) {
        try {
            val shapeFile = ShapeFile(file)
            var record: ESRIRecord? = shapeFile.nextRecord
            while (record != null) {
                if (ShapeUtils.getStringForType(record.shapeType) == "POINT") {
                    val polyRec = record as ESRIPointRecord
                    val point = ESIRPoint()
                    point.position = LatLng(polyRec.y, polyRec.x)
                    _esirPoints.add(point)
                }
                record = shapeFile.nextRecord
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readDbfFile(file: File) {
        val dbfStream = DbfInputStreamReader(FileInputStream(file))
        for (i in 0 until _esirPoints.size)
            _esirPoints[i].title = (dbfStream.records[i] as ArrayList<*>)[3].toString()

    }

    override fun getLayout(map: GoogleMap): PointLayer {
        draw(map)
        return this
    }

    private fun draw(map: GoogleMap) {
        for (point in _esirPoints) {
            val circleOptions = CircleOptions()
                .strokeColor(Color.argb(150, 200, 0, 0))
                .fillColor(Color.argb(150, 200, 0, 0))
                .strokeWidth(16.0f)
                .center(point.position)
                .radius(400.0)
                .visible(false)
                .zIndex(1f)
            circles.add(map.addCircle(circleOptions))
            textOverlays.add(drawText(map, point.title, point.position, 128f, Color.argb(150, 0, 0, 220), 1000f))
        }
    }

    private fun drawText(
        map: GoogleMap,
        text: String,
        position: LatLng,
        textSize: Float,
        textColor: Int,
        charSize: Float
    ): GroundOverlay {
        val groundOptions = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromBitmap(textToBitmap(text, textSize, textColor)))
            .anchor(.5f, 1.0f)
            .position(position, text.length * charSize, charSize * 2.1f)
            .visible(false)
            .zIndex(1f)
        return map.addGroundOverlay(groundOptions)
    }

    override fun updateVisibility(bounds: LatLngBounds) {
        for (i in 0 until circles.size) {
            var visible = false
            if (bounds.contains(circles[i].center))
                visible = true
            if (visible != circles[i].isVisible) {
                circles[i].isVisible = visible
                textOverlays[i].isVisible = visible
            }
        }
    }

    private fun textToBitmap(text: String, textSize: Float, textColor: Int): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            .also {
                it.textSize = textSize
                it.color = textColor
                it.textAlign = Paint.Align.LEFT
            }
        val baseline = -paint.ascent()
        val width = (paint.measureText(text) + 0.5f).toInt()
        val height = (baseline + paint.descent() + 0.5f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(text, 0f, baseline, paint)
        return image
    }
}