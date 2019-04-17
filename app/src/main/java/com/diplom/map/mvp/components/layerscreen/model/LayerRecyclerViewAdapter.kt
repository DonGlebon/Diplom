package com.diplom.map.mvp.components.layerscreen.model

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.entities.LayerVisibility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layer_card.view.*
import javax.inject.Inject

class LayerRecyclerViewAdapter(private var itemList: List<LayerVisibility>, private val mContext: Context) :
    RecyclerView.Adapter<LayerViewHolder>() {

    @Inject
    lateinit var db: AppDatabase

    init {
        App.get().injector.inject(this)
    }

    private val disposable = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layer_card, parent, false) as View
        return LayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: LayerViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val view = holder.view.expandable_cardview
        view.setValues(itemList[position])
        view.setOnLayerStateWasChangedListener { it, type ->
            if (type == "Save")
                disposable.add(db.layerDao().updateLayerVisibility(
                    it.uid,
                    it.ZIndex,
                    it.minZoom,
                    it.maxZoom
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { }
                    .doOnError { }
                    .doOnComplete { Log.d("Hello", "Complete") }
                    .subscribe()
                )
            else {
                Log.d("Hello", "Subscribe ${it.uid}")
                disposable.add(db.layerDao().deleteLayer(it.uid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { Log.d("Hello", "Subscribe ") }
                    .doOnComplete { Log.d("Hello", "Succes") }
                    .doOnError { Log.d("Hello", "Error") }
                    .subscribe()
                )
            }
        }
        view.setOnVisibilityChangedListener(
            object : ExpandableCardView.OnVisibilityChangedListener {
                override fun VisibilityChanged(isVisible: Boolean) {
                    Log.d("Hello", "Visibility: $isVisible")
                }
            })
    }
}

class LayerViewHolder(val view: View) : RecyclerView.ViewHolder(view)