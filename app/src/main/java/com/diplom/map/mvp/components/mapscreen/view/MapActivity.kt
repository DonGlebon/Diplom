package com.diplom.map.mvp.components.mapscreen.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseCompatActivity
import com.diplom.map.mvp.components.layerscreen.view.LayerActivity
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MapActivity : BaseCompatActivity(), MapScreenContract.View, OnMapReadyCallback {

    @Inject
    lateinit var presenter: MapScreenPresenter
    private lateinit var mMap: GoogleMap

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_maps)
        App.get().injector.inject(this)
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
        mMap.setOnCameraIdleListener {
            displayLayer()
            Log.d("Hello", "Zoom: ${mMap.cameraPosition.zoom}")
        }
    }

    val disposable = CompositeDisposable()

    fun displayLayer() {
        val s = System.currentTimeMillis()
        val bounds = mMap.projection.visibleRegion.latLngBounds
        val north = bounds.northeast.latitude
        val east = bounds.northeast.longitude
        val south = bounds.southwest.latitude
        val west = bounds.southwest.longitude
        if (mMap.cameraPosition.zoom > 15) {
            disposable.add(presenter.db.pointDao().getPointsInBounds(north, east, south, west)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { visiblePolygons ->
                        presenter.db.polygonDao().getPolygonsWithPoints(visiblePolygons)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                { pData ->
                                    mMap.clear()
                                    for (polygon in pData) {
                                        val points = ArrayList<LatLng>()
                                        for (point in polygon.points.sortedBy { it.uid }) {
                                            points.add(
                                                LatLng(
                                                    point.Lat,
                                                    point.Lng
                                                )
                                            )
                                        }
                                        mMap.addPolygon(
                                            PolygonOptions()
                                                .addAll(points)
                                                .strokeColor(Color.argb(150, 20, 240, 40))
                                                .fillColor(Color.argb(150, 140, 20, 140))
                                                .strokeWidth(4.0f)
                                                .geodesic(true)

                                        )
                                    }
                                },
                                {},
                                {}
                            )
                    },
                    { },
                    {

                    }
                )
            )
        } else
            mMap.clear()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_layers -> {
            startActivity(Intent(this, LayerActivity::class.java))
            true
        }
        R.id.action_select -> {

            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
