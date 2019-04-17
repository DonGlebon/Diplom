package com.diplom.map.esri.model

import com.google.android.gms.maps.model.TileOverlay

class ESRITileProviderCollector() {

    private val tileProviders = ArrayList<ESRITileProvider>()
    private val mapProviders = ArrayList<TileOverlay>()

    fun addTileProvider(provider: ESRITileProvider) {
        if (isContainAndChanged(provider) > 0) {

        }
    }

    private fun isContainAndChanged(provider: ESRITileProvider): Long {
        var layerId = -1L
//        for (tile in tileProviders) {
//            if (tile.)
//        }
        return layerId
    }
}