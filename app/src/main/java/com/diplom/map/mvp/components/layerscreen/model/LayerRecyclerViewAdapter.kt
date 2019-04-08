package com.diplom.map.mvp.components.layerscreen.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.map.R
import com.diplom.map.room.entities.Layer
import kotlinx.android.synthetic.main.layer_card.view.*

class LayerRecyclerViewAdapter(private var adapter: List<Layer>, private val mContext: Context) :
    RecyclerView.Adapter<LayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layer_card, parent, false) as View
        return LayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adapter.size
    }

    override fun onBindViewHolder(holder: LayerViewHolder, position: Int) {
        holder.view.tv_layer_name.text = mContext.getString(R.string.layer_name, adapter[position].name)
        holder.view.tv_layer_path.text = mContext.getString(R.string.layer_path, adapter[position].path)
    }

}

class LayerViewHolder(val view: View) : RecyclerView.ViewHolder(view)