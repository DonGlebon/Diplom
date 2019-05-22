package com.diplom.map.mvp.components.map.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.diplom.map.R
import com.diplom.map.location.LocationProvider
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.map.contract.MapFragmentContract
import com.diplom.map.mvp.components.map.model.MyInfoWindowAdapter
import com.diplom.map.mvp.components.map.presenter.MapFragmentPresenter
import com.diplom.map.utils.model.ESRITileProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_page_map.*
import kotlinx.android.synthetic.main.marker_layout.view.*
import javax.inject.Inject

class MapFragment : BaseFragment(), MapFragmentContract.View, OnMapReadyCallback {

    private lateinit var mMapView: MapView
    private var mMap: GoogleMap? = null
    private var marker: Marker? = null
    private var polygonList = ArrayList<Polygon>()
    private var userPath: Polyline? = null

    @Inject
    lateinit var presenter: MapFragmentPresenter

    @Inject
    lateinit var locationProvider: LocationProvider

    override fun init(savedInstanceState: Bundle?) {
        App.get().injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter.attach(this)
        val rootView = inflater.inflate(R.layout.fragment_page_map, container, false)
        prepareMap(rootView, savedInstanceState)
        return rootView
    }

    private fun prepareMap(parent: View, savedInstanceState: Bundle?) {
        mMapView = parent.findViewById(R.id.mapView)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()
        MapsInitializer.initialize(activity!!.application)
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        setupMap()
        loadMarkers()
        addListeners()
    }

    private fun setupMap() {
        userPath = mMap?.addPolyline(PolylineOptions())
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        )
            mMap?.isMyLocationEnabled = true
        presenter.mapReady()

        Single.just(
            LatLngBounds(
                LatLng(53.4958349064697316, 26.8924356412861592),
                LatLng(53.6616809839191902, 27.3830005533529253)
            )
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(it, 1))
            }
            .subscribe()
    }

    private fun addListeners() {
        locationProvider.setOnLocationChangeListener {
            userPath?.remove()
            userPath = mMap?.addPolyline(
                PolylineOptions().addAll(it.map { location ->
                    LatLng(location.latitude, location.longitude)
                })
                    .color(Color.RED)
                    .visible(true)
                    .width(10f)
            )
        }

        addMarkerButton.setOnClickListener {
            addMarker()
        }

        getBoundsButton?.setOnClickListener {
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds(
                        LatLng(53.4958349064697316, 26.8924356412861592),
                        LatLng(53.6616809839191902, 27.3830005533529253)
                    ),
                    1
                )
            )
        }

        mMap?.setOnInfoWindowClickListener { marker ->
            if (marker.tag.toString().contains("Marker:")) {
                val id = marker.tag.toString().split(':')[1].toLong()
                presenter.db.layerDao().getMarker(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        val view = LayoutInflater.from(this.activity!!).inflate(R.layout.dialog_add_marker, null, false)
                        val titleView = view.findViewById<EditText>(R.id.addMarkerTitle)
                        val snippetView = view.findViewById<EditText>(R.id.addTitleSnippet)
                        titleView.setText(it.title)
                        snippetView.setText(it.snippet)
                        AlertDialog.Builder(this.activity!!)
                            .setTitle("Добавить новый маркер")
                            .setView(view)
                            .setNegativeButton("Удалить") { _, _ -> deleteMarker(it,marker) }
                            .setPositiveButton("Обновить") { _, _ ->
                                val newMarker = it
                                newMarker.title = view.findViewById<EditText>(R.id.addMarkerTitle).text.toString()
                                newMarker.snippet = view.findViewById<EditText>(R.id.addTitleSnippet).text.toString()
                                updateMarker(newMarker)
                            }
                            .create()
                            .show()
                    }
                    .doOnError { }
                    .subscribe()
            } else {
                val id = marker.title.toLong()
                presenter.getLayerDataById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { fdData ->
                        val map = HashMap<String, String>()
                        for (data in fdData)
                            map[data.ColumnName] = data.value
                        map
                    }
                    .doOnSuccess {
                        createDialog(it)
                    }
                    .doOnError { }
                    .subscribe()
            }

        }
    }

    private fun deleteMarker(m: com.diplom.map.room.entities.Marker,marker: Marker) {
        presenter.db.layerDao().deleteMarker(m)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { marker.remove() }
            .doOnError { }
            .subscribe()
    }

    private fun updateMarker(newMarker: com.diplom.map.room.entities.Marker) {
        presenter.db.layerDao().updateMarker(newMarker)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                marker?.remove()
                mMap?.addMarker(
                    MarkerOptions()
                        .title(newMarker.title)
                        .snippet(newMarker.snippet)
                        .position(LatLng(newMarker.lat, newMarker.lng))
                )?.tag = "Marker:${newMarker.uid}"
            }
            .subscribe()
    }

    private fun addMarker() {
        locationProvider.getLocation()?.addOnSuccessListener {
            val view = LayoutInflater.from(this.activity!!).inflate(R.layout.dialog_add_marker, null, false)
            AlertDialog.Builder(this.activity!!)
                .setTitle("Добавить новый маркер")
                .setView(view)
                .setNegativeButton("Отменить") { _, _ -> }
                .setPositiveButton("Добавить") { _, _ ->
                    val title = view.findViewById<EditText>(R.id.addMarkerTitle).text.toString()
                    val snippet = view.findViewById<EditText>(R.id.addTitleSnippet).text.toString()
                    val lat = it.latitude
                    val lng = it.longitude
                    saveMarker(title, snippet, lat, lng)
                }
                .create()
                .show()
        }
    }

    private fun saveMarker(title: String, snippet: String, lat: Double, lng: Double) {
        presenter.db.layerDao().addMarker(
            com.diplom.map.room.entities
                .Marker(0, title, snippet, lat, lng)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                mMap?.addMarker(
                    MarkerOptions()
                        .title(title)
                        .snippet(snippet)
                        .position(LatLng(lat, lng))
                )?.tag = "Marker:$it"
            }
            .subscribe()
    }

    private fun loadMarkers() {
        presenter.db.layerDao().getAllMarkers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                for (marker in it)
                    mMap?.addMarker(
                        MarkerOptions()
                            .title(marker.title)
                            .snippet(marker.snippet)
                            .position(LatLng(marker.lat, marker.lng))
                    )?.tag = "Marker:${marker.uid}"
            }
            .doOnError { }
            .subscribe()
    }


    var tileOverlay: TileOverlay? = null
    override fun addTileProvider(provider: ESRITileProvider) {
        tileOverlay?.remove()
        tileOverlay = mMap?.addTileOverlay(
            TileOverlayOptions()
                .fadeIn(true)
                .tileProvider(provider)
        )
        mMap?.setInfoWindowAdapter(MyInfoWindowAdapter(this.context))
        mMap?.setOnMapClickListener {
            val uid = provider.getPolygonByClick(mMap!!, it, mMap!!.cameraPosition.zoom)
            if (uid != -1L) {
                polygonList.forEach { polygon -> polygon.remove() }
                polygonList.clear()
                presenter.getFeatureById(uid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess { subFeatures ->
                        for (subFeature in subFeatures) {
                            val points = ArrayList<LatLng>()
                            for (pointsData in subFeature.points.sortedWith(compareBy { it.uid }))
                                points.add(LatLng(pointsData.Lat, pointsData.Lng))
                            polygonList.add(
                                mMap!!.addPolygon(
                                    PolygonOptions()
                                        .addAll(points)
                                        .fillColor(Color.RED)
                                        .strokeColor(Color.CYAN)
                                        .zIndex(10f)
                                )
                            )
                        }
                    }
                    .doOnError { }
                    .subscribe()
                presenter.getLayerDataById(uid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess { data ->
                        mMap?.animateCamera(CameraUpdateFactory.newLatLng(it))
                        marker?.remove()
                        var snippet = ""
                        for (row in data) {
                            val value = row.value
                            if (!value.isEmpty()) {
                                snippet += "${row.ColumnName}: $value\n"
                            }
                        }
                        mMap?.animateCamera(CameraUpdateFactory.newLatLng(it))
                        marker?.remove()
                        marker = mMap!!.addMarker(
                            MarkerOptions()
                                .title("$uid")
                                .snippet(snippet)
                                .position(it)
                                .infoWindowAnchor(0.5f, 0f)
                        )
                        marker!!.showInfoWindow()
                    }
                    .doOnError { error -> Log.d("Hello", "Marker err: ${error.message}") }
                    .subscribe()
            }
        }
    }

    private fun createDialog(map: HashMap<String, String>) {
        val view =
            LayoutInflater.from(this.context).inflate(R.layout.marker_layout, null, false)
        for (key in map.keys) {
            val layout = LinearLayout(this.context)
            layout.orientation = LinearLayout.HORIZONTAL
            view.markerLayout.addView(
                layout,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            val textView = TextView(this.context)
            textView.text = "$key: "
            layout.addView(
                textView,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            val editText = EditText(this.context)
            editText.setText(map[key])
            layout.addView(
                editText,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }
        AlertDialog.Builder(this.activity!!)
            .setTitle("")
            .setView(view)
            .setPositiveButton("Сохранить") { _, _ ->
                Log.d("Hello", "1")
            }
            .create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }
}

