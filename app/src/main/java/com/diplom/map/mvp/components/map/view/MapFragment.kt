package com.diplom.map.mvp.components.map.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.map.contract.MapFragmentContract
import com.diplom.map.mvp.components.map.presenter.MapFragmentPresenter
import com.diplom.map.esri.model.ESRITileProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlayOptions
import javax.inject.Inject

class MapFragment : BaseFragment(), MapFragmentContract.View, OnMapReadyCallback {

    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    @Inject
    lateinit var presenter: MapFragmentPresenter

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
        presenter.mapReady()
    }


    override fun addTileProvider(provider: ESRITileProvider) {
        mMap.addTileOverlay(
            TileOverlayOptions()
                .fadeIn(true)
                .tileProvider(provider)
        )
    }

}
