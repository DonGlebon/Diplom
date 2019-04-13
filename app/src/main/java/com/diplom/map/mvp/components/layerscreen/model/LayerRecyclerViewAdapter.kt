package com.diplom.map.mvp.components.layerscreen.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.map.R
import com.diplom.map.room.entities.Layer

class LayerRecyclerViewAdapter(private var adapter: List<Layer>, private val mContext: Context) :
    RecyclerView.Adapter<LayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false) as View
        return LayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adapter.size
    }

    override fun onBindViewHolder(holder: LayerViewHolder, position: Int) {
        holder.setIsRecyclable(false)

    }
}

class LayerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

}