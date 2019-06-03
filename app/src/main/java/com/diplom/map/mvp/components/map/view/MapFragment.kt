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
import android.widget.ScrollView
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
    private var userPoints = ArrayList<Circle>()
    private var userAreas = ArrayList<Circle>()

    @Inject
    lateinit var presenter: MapFragmentPresenter

    @Inject
    lateinit var locationProvider: LocationProvider

    private val disposable = CompositeDisposable()

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

//    override fun onResume() {
//        super.onResume()
//        val it =LatLngBounds(
//            LatLng(53.4958349064697316, 26.8924356412861592),
//            LatLng(53.6616809839191902, 27.3830005533529253)
//        )
//        mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(it, 1))
//    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap?.setOnMapLoadedCallback {
            val it = LatLngBounds(
                LatLng(53.4958349064697316, 26.8924356412861592),
                LatLng(53.6616809839191902, 27.3830005533529253)
            )
            mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(it, 1))
        }
        Log.d("Hello", "1")
        setupMap()
        Log.d("Hello", "2")
        loadMarkers()
        Log.d("Hello", "3")
        addListeners()
        Log.d("Hello", "4")
//        Single.just(
//            LatLngBounds(
//                LatLng(53.4958349064697316, 26.8924356412861592),
//                LatLng(53.6616809839191902, 27.3830005533529253)
//            )
//        ).subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSuccess {
//                Log.d("Hello", "5")
//            }
//            .subscribe()
    }

    private fun setupMap() {
        mMap?.setInfoWindowAdapter(MyInfoWindowAdapter(this.context))
        userPath = mMap?.addPolyline(PolylineOptions())
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        )
            mMap?.isMyLocationEnabled = true
        presenter.mapReady()

    }

    private fun addListeners() {
        locationProvider.setOnLocationChangeListener { locations ->
            userPath?.remove()
            userPath = mMap?.addPolyline(
                PolylineOptions().addAll(locations.map { location ->
                    LatLng(location.latitude, location.longitude)
                })
                    .color(Color.RED)
                    .visible(true)
                    .width(10f)
                    .zIndex(0f)
            )
            userAreas.forEach { it.remove() }
            userPoints.forEach { it.remove() }
            userAreas.clear()
            userPoints.clear()
            if (mMap != null)
                for (location in locations) {
                    userAreas.add(
                        mMap!!.addCircle(
                            CircleOptions()
                                .center(LatLng(location.latitude, location.longitude))
                                .radius(location.accuracy.toDouble())
                                .fillColor(Color.argb(60, 20, 40, 250))
                                .strokeColor(Color.argb(120, 20, 40, 250))
                                .strokeWidth(3f)
                                .zIndex(1f)
                        )
                    )
                    userPoints.add(
                        mMap!!.addCircle(
                            CircleOptions()
                                .center(LatLng(location.latitude, location.longitude))
                                .radius(2.0)
                                .fillColor(Color.argb(160, 20, 250, 100))
                                .strokeColor(Color.argb(255, 255, 250, 255))
                                .strokeWidth(1f)
                                .zIndex(2f)
                        )
                    )
                }
        }

        addMarkerButton?.setOnClickListener {
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
                disposable.add(presenter.db.layerDao().getMarker(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        val view = LayoutInflater.from(this.activity!!).inflate(R.layout.dialog_add_marker, null, false)
                        val titleView = view.findViewById<EditText>(R.id.addMarkerTitle)
                        val snippetView = view.findViewById<EditText>(R.id.addTitleSnippet)
                        titleView.setText(it.title)
                        snippetView.setText(it.snippet)
                        AlertDialog.Builder(this.activity!!)
                            .setTitle("Изменить заметку")
                            .setView(view)
                            .setNegativeButton("Удалить") { _, _ -> deleteMarker(it, marker) }
                            .setPositiveButton("Обновить") { _, _ ->
                                val newMarker = it
                                newMarker.title = view.findViewById<EditText>(R.id.addMarkerTitle).text.toString()
                                newMarker.snippet = view.findViewById<EditText>(R.id.addTitleSnippet).text.toString()
                                updateMarker(newMarker, marker)
                            }
                            .create()
                            .show()
                    }
                    .doOnError { }
                    .subscribe()
                )
            } else {
                val id = marker.title.toLong()
                disposable.add(presenter.getLayerDataById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { fdData ->
                        val map = HashMap<String, String>()
                        for (data in fdData)
                            map[data.ColumnName] = data.value
                        map
                    }
                    .doOnSuccess {
                        createDialog(id, it)
                    }
                    .doOnError { }
                    .subscribe()
                )
            }

        }
    }

    private fun deleteMarker(m: com.diplom.map.room.entities.Marker, marker: Marker) {
        disposable.add(presenter.db.layerDao().deleteMarker(m)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { marker.remove() }
            .doOnError { }
            .subscribe()
        )
    }

    private fun updateMarker(newMarker: com.diplom.map.room.entities.Marker, oldMarker: Marker) {
        disposable.add(
            presenter.db.layerDao().updateMarker(newMarker)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    oldMarker.remove()
                    mMap?.addMarker(
                        MarkerOptions()
                            .title(newMarker.title)
                            .snippet("${newMarker.snippet}\nLat: ${newMarker.lat}\nLng: ${newMarker.lng}")
                            .position(LatLng(newMarker.lat, newMarker.lng))
                    )?.tag = "Marker:${newMarker.uid}"
                }
                .subscribe()
        )
    }

    private fun addMarker() {
        locationProvider.getLocation()?.addOnSuccessListener {
            val view = LayoutInflater.from(this.activity!!).inflate(R.layout.dialog_add_marker, null, false)
            AlertDialog.Builder(this.activity!!)
                .setTitle("Добавить новую заметку")
                .setView(view)
                .setCancelable(false)
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
        disposable.add(presenter.db.layerDao().addMarker(
            com.diplom.map.room.entities
                .Marker(0, title, snippet, lat, lng)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                mMap?.addMarker(
                    MarkerOptions()
                        .title(title)
                        .snippet("$snippet\nLat: $lat\nLng: $lng")
                        .position(LatLng(lat, lng))
                )?.tag = "Marker:$it"
            }
            .subscribe()
        )
    }

    private fun loadMarkers() {
        disposable.add(presenter.db.layerDao().getAllMarkers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                for (marker in it)
                    mMap?.addMarker(
                        MarkerOptions()
                            .title(marker.title)
                            .snippet(marker.snippet + "\nLat: ${marker.lat}\nLng: ${marker.lng}")
                            .position(LatLng(marker.lat, marker.lng))
                    )?.tag = "Marker:${marker.uid}"
            }
            .doOnError { }
            .subscribe()
        )
    }


    var tileOverlay: TileOverlay? = null
    override fun addTileProvider(provider: ESRITileProvider) {
        tileOverlay?.remove()
        tileOverlay = mMap?.addTileOverlay(
            TileOverlayOptions()
                .fadeIn(true)
                .tileProvider(provider)
        )
        mMap?.setOnMapClickListener {
            val uid = provider.getPolygonByClick(mMap!!, it, mMap!!.cameraPosition.zoom)
            Log.d("Hello", "polygon Click: $uid")
            if (uid != -1L) {
                polygonList.forEach { polygon -> polygon.remove() }
                polygonList.clear()
                disposable.add(presenter.getFeatureById(uid)
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
                )
                disposable.add(presenter.getLayerDataById(uid)
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
                        marker = mMap?.addMarker(
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
                )
            }
        }
    }

    private fun createDialog(id: Long, map: HashMap<String, String>) {
        val view =
            LayoutInflater.from(this.context).inflate(R.layout.marker_layout, null, false)
        for (key in map.keys) {
            val layout = LinearLayout(this.context)
            layout.orientation = LinearLayout.HORIZONTAL
            view.markerLayout.addView(
                layout,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            val textView = TextView(this.context)
            textView.text = "$key: "
            layout.addView(
                textView,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.weight = 1f; it.leftMargin = 8 }
            )

            val editText = EditText(this.context)
            editText.setText(map[key])
            layout.addView(
                editText,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.weight = 1f;it.rightMargin = 8 }
            )
        }
        view.findViewById<TextView>(R.id.markerTitle).text = "$id"
        view.findViewById<ScrollView>(R.id.scrollMarker).layoutParams =  LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        AlertDialog.Builder(this.activity!!)
            .setTitle("")
            .setView(view)
            .setNegativeButton("Отмена") { _, _ ->
                Log.d("Hello", "1")
            }
            .setPositiveButton("Сохранить") { _, _ ->
                Log.d("Hello", "1")
            }
            .create().show()
    }

    override fun onDetach() {
        disposable.clear()
        presenter.detach()
        Log.d("Hello", "5")
        mMap = null
        super.onDetach()
    }

}

