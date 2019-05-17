package com.diplom.map.mvp.components.map.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.diplom.map.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MyInfoWindowAdapter(val mContext: Context) : GoogleMap.InfoWindowAdapter {

    private val view: View = LayoutInflater.from(mContext).inflate(R.layout.marker_layout, null)

    override fun getInfoContents(marker: Marker?): View {
        val title = view.findViewById<TextView>(R.id.markerTitle)
        title.text = marker?.title ?: ""
        val snippet = view.findViewById<TextView>(R.id.markerSnippet)
        snippet.text = marker?.snippet ?: ""
        return view
    }

    override fun getInfoWindow(marker: Marker?): View {
        val title = view.findViewById<TextView>(R.id.markerTitle)
        title.text = marker?.title ?: ""
        val snippet = view.findViewById<TextView>(R.id.markerSnippet)
        snippet.text = marker?.snippet ?: ""
        return view
    }
}