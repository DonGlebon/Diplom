package com.diplom.map.esri.model

import com.diplom.map.room.data.LayerData
import com.google.android.gms.maps.model.TileProvider

abstract class ESRITileProvider : TileProvider {
    abstract fun addLayer(layerData: LayerData)
}