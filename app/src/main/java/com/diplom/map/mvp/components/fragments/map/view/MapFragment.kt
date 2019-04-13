package com.diplom.map.mvp.components.fragments.map.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.fragments.map.presenter.MapFragmentPresenter
import com.google.android.gms.maps.*
import javax.inject.Inject

class MapFragment : BaseFragment(), OnMapReadyCallback {

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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(presenter.lastPosition))
        mMap.setOnCameraIdleListener {
            presenter.setLastPlace(
                mMap.projection.visibleRegion.latLngBounds.center,
                mMap.cameraPosition.zoom
            )
        }
    }

}
