package com.diplom.map.utils.model

import com.diplom.map.room.data.LayerData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider

abstract class
ESRITileProvider : TileProvider {
    abstract fun addLayer(layerData: LayerData)
    abstract fun getPolygonByClick(map: GoogleMap, position: LatLng, zoom: Float): Long
}