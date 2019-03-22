package com.diplom.map.layers.utils

import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polygon

class VisibleTask : AsyncTask<Unit, Int, Boolean>() {

    lateinit var polygons: ArrayList<Polygon>
    lateinit var bounds: LatLngBounds

    private var points = ArrayList<ArrayList<LatLng>>()

    var s: Long = 0

    override fun onPreExecute() {
        super.onPreExecute()
        s = System.currentTimeMillis()
        for (polygon in polygons) {
            polygon.isVisible = false
            points.add(polygon.points as ArrayList<LatLng>)
        }
    }

    override fun doInBackground(vararg params: Unit): Boolean {
        s = System.currentTimeMillis()
        for (i in 0 until polygons.size) {
            for (point in points[i]) {
                if (bounds.contains(point)) {
                    publishProgress(i)
                    break
                }
            }
        }
        Log.d("Hello", "end ${System.currentTimeMillis() - s}")
        return false
    }


    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        //polygons[values[0]!!].isVisible = true
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
    }
}