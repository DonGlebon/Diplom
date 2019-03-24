package com.diplom.map.layers

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds

interface GEOLayer<V> {

    fun getLayout(map: GoogleMap): V

    fun updateVisibility(bounds: LatLngBounds, zoom: Float)

}