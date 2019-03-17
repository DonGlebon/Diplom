package com.diplom.map.mvp.components.mapscreen.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.*
import com.diplom.map.DbfInputStreamReader
import com.diplom.map.GeoPolyLayer
import com.diplom.map.LayerActivity
import com.diplom.map.R
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import com.diplom.map.mvp.abstracts.view.BaseCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.File
import java.io.FileInputStream


class MapsActivity : BaseCompatActivity(), MapScreenContract.View, OnMapReadyCallback {

    private val presenter = MapScreenPresenter()
    private lateinit var mMap: GoogleMap
    private var pointCount = 0
    private var polyCount = 0

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_maps)
        presenter.attach(this)
        setupPermissions()
        setSupportActionBar(findViewById(R.id.toolBar))
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val sydney = LatLng(53.551413, 27.057378)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        val lakes = GeoPolyLayer(mMap, "alaska", "/storage/emulated/0/Map/")
        mMap.setOnCameraIdleListener { lakes.updateVisibility(mMap.projection.visibleRegion.latLngBounds) }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        }
    }

    private fun readShapefile() {
        try {
            val targetFilePath = "/storage/emulated/0/Map/lakes.shp"
            val shapeFile = ShapeFile(targetFilePath)
            val fileInputStream = FileInputStream(File("/storage/emulated/0/Map/ref.dbf"))
            val dbfInputStream = DbfInputStreamReader(fileInputStream)
            var esriRecord: ESRIRecord? = shapeFile.nextRecord
            while (esriRecord != null) {
                val shapeTypeStr = ShapeUtils.getStringForType(esriRecord.shapeType)
                if (shapeTypeStr == "POLYGON") {
                    val polyRec = esriRecord as ESRIPolygonRecord?
                    polyRec!!.polygons.size
                    for (i in polyRec.polygons.indices) {
                        drawESIRPolygon(
                            mMap,
                            polyRec.polygons[i] as ESRIPoly.ESRIFloatPoly,
                            Color.argb(150, 200, 0, 0),
                            Color.argb(150, 0, 0, 150),
                            2.0f
                        )
                    }
                } else if (shapeTypeStr == "POINT") {
                    polyCount++
                    val pol = drawESIRPoint(mMap, esriRecord as ESRIPointRecord)
                    val pointName = (dbfInputStream.records[polyCount] as ArrayList<*>)[3]
                    drawText(mMap, pointName.toString(), pol.center, 128f, Color.argb(150, 255, 0, 0), 1000f)

                }
                esriRecord = shapeFile.nextRecord
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        groundOptions.image(BitmapDescriptorFactory.fromBitmap(textToBitmap(text, textSize, textColor)))
        groundOptions.anchor(.5f, 1.0f)
        groundOptions.position(position, text.length * charSize, charSize * 2.1f)
        return map.addGroundOverlay(groundOptions)
    }

    private fun drawESIRPolygon(
        map: GoogleMap,
        poly: ESRIPoly.ESRIFloatPoly,
        strokeColor: Int,
        fillColor: Int,
        strokeWidth: Float
    ): Polygon {
        val polygonOptions = PolygonOptions().apply {
            strokeColor(strokeColor)
            fillColor(fillColor)
            strokeWidth(strokeWidth)
        }
        pointCount += poly.nPoints
        for (j in 0 until poly.nPoints) {
            polygonOptions.add(LatLng(poly.getY(j), poly.getX(j)))
        }
        polyCount++
        return map.addPolygon(polygonOptions)
    }

    private fun drawESIRPoint(map: GoogleMap, point: ESRIPointRecord): Circle {
        val polygonOptions = CircleOptions()
        polygonOptions.strokeColor(Color.argb(150, 200, 0, 0))
        polygonOptions.fillColor(Color.argb(150, 200, 0, 0))
        polygonOptions.strokeWidth(20.0f)
        polygonOptions.center(LatLng(point.y, point.x))
        polygonOptions.radius(400.0)
        return map.addCircle(polygonOptions)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_layers -> {
            startActivity(Intent(this, LayerActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
