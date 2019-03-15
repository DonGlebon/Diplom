package com.diplom.map

import com.google.android.gms.maps.GoogleMap

interface GEOLayer {
    val map: GoogleMap
    var isVisible: Boolean
    fun setVisibility(visibility: Boolean)
}