package com.diplom.map.mvp.components.layerstyle.model

import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diplom.map.MainActivity
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.data.ThemeStyleValuesData
import com.diplom.map.room.entities.ThemeStyleValues
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class StyleRecyclerViewAdapter(
    private val activity: MainActivity,
    private val data: List<ThemeStyleValuesData>,
    val disposable: CompositeDisposable
) :
    RecyclerView.Adapter<StyleRecyclerViewAdapter.StyleViewHolder>() {

    @Inject
    lateinit var db: AppDatabase

    //   private val disposable = CompositeDisposable()

    init {
        App.get().injector.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.style_card, parent, false) as View
        return StyleViewHolder(activity, view)
    }

    override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
        holder.title.text = if (data[position].value.isEmpty()) "Без значения" else data[position].value
        holder.fill.setBackgroundColor(data[position].fillColor)
        holder.stroke.setBackgroundColor(data[position].strokeColor)
        holder.strokeWidth.setText(data[position].strokeWidth.toString())
        holder.colorFillPicker.setCallback {
            data[position].fillColor = it
            updateStyle(data[position])
            holder.fill.setBackgroundColor(it)
        }
        holder.colorStrokePicker.setCallback {
            data[position].strokeColor = it
            updateStyle(data[position])
            holder.stroke.setBackgroundColor(it)
        }
        holder.strokeWidth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        holder.strokeWidth.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                data[position].strokeWidth = holder.strokeWidth.text.toString().toFloat()
                updateStyle(data[position])
            }
        }
    }

    private fun updateStyle(styleData: ThemeStyleValuesData) {
        val style = ThemeStyleValues(
            styleData.uid,
            styleData.themeId,
            styleData.value,
            styleData.fillColor,
            styleData.strokeColor,
            styleData.strokeWidth
        )
        disposable.add(
            db.layerDao().updateThemeValue(style)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.d("Hello", "Update theme err: ${it.message}") }
                .subscribe()
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class StyleViewHolder(activity: MainActivity, view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.styleTitle)
        val fill: ImageView = view.findViewById(R.id.imageFill)
        val stroke: ImageView = view.findViewById(R.id.imageStroke)
        val strokeWidth: EditText = view.findViewById(R.id.styleWidth)
        val colorFillPicker: ColorPicker = ColorPicker(activity, 0, 0, 0).also { it.enableAutoClose() }
        val colorStrokePicker: ColorPicker = ColorPicker(activity, 0, 0, 0).also { it.enableAutoClose() }

        init {
            fill.setOnClickListener {
                colorFillPicker.color = (fill.background as ColorDrawable).color
                colorFillPicker.show()
            }
            stroke.setOnClickListener {
                colorStrokePicker.color = (stroke.background as ColorDrawable).color
                colorStrokePicker.show()
            }
        }
    }

}

