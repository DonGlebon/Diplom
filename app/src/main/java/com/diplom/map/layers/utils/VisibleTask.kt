package com.diplom.map.layers.utils

import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polygon

class VisibleTask : AsyncTask<ArrayList<LatLng>, Unit, Boolean>() {

    lateinit var polygon: Polygon
    lateinit var bounds: LatLngBounds

    override fun doInBackground(vararg params: ArrayList<LatLng>): Boolean {
        for (point in params[0]) {
            if (bounds.contains(point)) {
                return true
            }
        }
        return false
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        polygon.isVisible = result
    }

}