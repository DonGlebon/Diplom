package com.diplom.map.mvp.components.mapscreen.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.bbn.openmap.layer.shape.ESRIPointRecord
import com.diplom.map.mvp.components.layerscreen.view.LayerActivity
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseCompatActivity
import com.diplom.map.mvp.components.mapscreen.contract.MapScreenContract
import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
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
        val layers = presenter.getLayout(mMap)
        mMap.setOnCameraIdleListener {
//            Observable.fromIterable(layers)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .doOnNext { it.updateVisibility(mMap.projection.visibleRegion.latLngBounds) }
//                .subscribe()

            for (layer in layers) {
                layer.updateVisibility(mMap.projection.visibleRegion.latLngBounds)
            }
        }
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

    private fun drawESIRPoint(map: GoogleMap, point: ESRIPointRecord): Circle {
        val polygonOptions = CircleOptions()
            .strokeColor(Color.argb(150, 200, 0, 0))
            .fillColor(Color.argb(150, 200, 0, 0))
            .strokeWidth(20.0f)
            .center(LatLng(point.y, point.x))
            .radius(400.0)
        return map.addCircle(polygonOptions)
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
