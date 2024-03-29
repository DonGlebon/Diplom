package com.diplom.map.mvp.components.layervisibility.model

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.diplom.map.R
import com.diplom.map.room.data.LayerVisibility
import kotlinx.android.synthetic.main.cardview_body.view.*
import kotlinx.android.synthetic.main.expandable_cardview.view.*

class ExpandableCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {

    //region View Data Visibility

    private var isEditMode = false

    private var values: LayerVisibility? = null

    fun setValues(values: LayerVisibility) {
        this.values = values
        displayValues()
    }

    private fun displayValues() {
        card_title.text = values!!.filename.capitalize().replace('_', ' ')
        visibilitySwitcher.isChecked = values?.isVisible!!
        ZIndexLayer.editText?.setText(values?.ZIndex.toString())
        maxZoomLayer.editText?.setText(values?.maxZoom.toString())
        minZoomLayer.editText?.setText(values?.minZoom.toString())
        val colums = HashMap<Long, String>()
        for (style in values!!.style)
            colums[style.uid] = style.columnName
        val adapterData = ArrayList<String>()
        for (v in colums.values)
            adapterData.add(v)
        var active = ""
        for (i in 0 until values?.style?.size!!)
            if (values?.themeId!! == values!!.style[i].uid) {
                active = values!!.style[i].columnName
                break
            }
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, adapterData)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerLayerStyle.adapter = adapter
        // Log.d("Hello", "Active: $activeId")
        spinnerLayerStyle.setSelection(adapterData.indexOf(active))
    }

    private fun isValuesWasChanged(): Boolean {
        return !(ZIndexLayer.editText?.text.toString() == values?.ZIndex.toString() &&
                maxZoomLayer.editText?.text.toString() == values?.maxZoom.toString() &&
                minZoomLayer.editText?.text.toString() == values?.minZoom.toString())
    }

    private fun onEditModeChange() {
        ZIndexEdit.isEnabled = isEditMode
        minZoomEdit.isEnabled = isEditMode
        maxZoomEdit.isEnabled = isEditMode
        if (isEditMode) {
            layoutEdit.visibility = View.VISIBLE
            layoutManager.visibility = View.GONE
        } else {
            layoutEdit.visibility = View.GONE
            layoutManager.visibility = View.VISIBLE
        }
    }

    //endregion

    //region View Initialization
    var innerView: View? = null
    private var title: String? = null
    private var innerViewID: Int = 0
    private var headerHeight: Int = 0
    private var bodyHeight: Int = 0
    private var isMoving = false
    var isExpanded: Boolean = true
        private set

    init {
        LayoutInflater.from(context).inflate(R.layout.expandable_cardview, this)
        attrs?.let {
            initAttrs(context, attrs)
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableCardView)
        title = typedArray.getString(R.styleable.ExpandableCardView_title)
        innerViewID = typedArray.getResourceId(R.styleable.ExpandableCardView_inner_view, View.NO_ID)
        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        card_layout.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        headerHeight = card_layout.measuredHeight
        setInnerView(innerViewID)
        card_layout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, headerHeight)
        initListeners()
    }

    private fun setInnerView(innerViewID: Int) {
        val container = this.findViewById<LinearLayout>(R.id.card_layout_container)
        innerView = LayoutInflater.from(context).inflate(innerViewID, this, false)
        container.addView(innerView)
        updateBodyHeight()
    }

    private fun updateBodyHeight() {
        innerView!!.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        bodyHeight = innerView!!.measuredHeight
    }
    //endregion

    //region View Animation

    private val ANIMATION_DURATION = 350L

    private fun animateArrow() {
        val animation = if (isExpanded)
            RotateAnimation(
                0f, 180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
        else
            RotateAnimation(
                180f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
        animation.fillAfter = true
        animation.duration = ANIMATION_DURATION
        expandButton.startAnimation(animation)
    }

    private fun animateExpanding() {
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                super.applyTransformation(interpolatedTime, t)
                val height = if (isExpanded)
                    ((headerHeight + bodyHeight) - bodyHeight * interpolatedTime).toInt()
                else
                    (headerHeight + bodyHeight * interpolatedTime).toInt()
                card_layout.layoutParams.height = height
                card_layout.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }

        }
        animation.duration = ANIMATION_DURATION
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                isMoving = false
            }

            override fun onAnimationStart(animation: Animation?) {
                isMoving = true
            }

        })
        startAnimation(animation)
    }
    //endregion

    //region View Listeners

    private var saveListener: OnLayerStateWasChangedListener? = null
    private var changeVisibilityListener: OnVisibilityChangedListener? = null
    private var themeListener: onThemeChangeListener? = null

    private val expandClickListener = OnClickListener {
        if (!isMoving) {
            animateExpanding()
            animateArrow()
            isExpanded = !isExpanded
        }
    }

    private fun initListeners() {

        card_header.setOnClickListener(expandClickListener)

        expandButton.setOnClickListener(expandClickListener)

        visibilitySwitcher.setOnCheckedChangeListener { _, isChecked ->
            changeVisibilityListener?.VisibilityChanged(isChecked,values!!.uid)
        }

        removeLayerButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Удалить слой?")
                .setMessage("Слой ${values?.filename} будет удален!")
                .setPositiveButton("Подтвердить") { _, _ -> saveListener?.onDelete(values!!) }
                .setNegativeButton("Отменить") { _, _ -> }
                .create()
                .show()
        }

        editLayerButton.setOnClickListener {
            isEditMode = true
            onEditModeChange()
        }

        discardButton.setOnClickListener {
            if (isValuesWasChanged())
                AlertDialog.Builder(context)
                    .setTitle("Отменить редактирование?")
                    .setMessage("Сделанные изменения не будут сохранены!")
                    .setNegativeButton("Продолжить") { _, _ -> }
                    .setPositiveButton("Отменить") { _, _ ->
                        isEditMode = false
                        displayValues()
                        onEditModeChange()
                    }
                    .create()
                    .show()
            else {
                isEditMode = false
                displayValues()
                onEditModeChange()
            }
        }

        saveButton.setOnClickListener {
            if (isValuesWasChanged())
                saveListener?.onSave(
                    LayerVisibility(
                        values?.uid!!,
                        values?.filename!!,
                        visibilitySwitcher.isChecked,
                        ZIndexLayer.editText?.text.toString().toInt(),
                        minZoomLayer.editText?.text.toString().toInt(),
                        maxZoomLayer.editText?.text.toString().toInt(),
                        values?.style!!,
                        values?.themeId!!
                    )
                )
        }

        spinnerLayerStyle.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d("Hello", "Active:nothing")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    var activeStyle = ""
                    for (i in 0 until values?.style?.size!!)
                        if (values?.themeId!! == values!!.style[i].uid) {
                            activeStyle = values!!.style[i].columnName
                            break
                        }
                    if (values != null && parent!!.adapter.getItem(position) != activeStyle) {
                        Log.d("Hello", "Sele 2")
                        val item = (parent?.adapter as ArrayAdapter<*>).getItem(position)
                        var active = 0L
                        for (value in values!!.style)
                            if (value.columnName == item) {
                                active = value.uid
                                break
                            }
                        themeListener?.onThemeChanged(values?.uid!!, active)
                    }
                }

            }
    }


    interface OnVisibilityChangedListener {
        fun VisibilityChanged(isVisible: Boolean, id: Long)
    }

    fun setOnVisibilityChangedListener(listener: OnVisibilityChangedListener) {
        changeVisibilityListener = listener
    }

    fun setOnVisibilityChangedListener(method: (isVisible: Boolean, uid: Long) -> Unit) {
        changeVisibilityListener = object :
            OnVisibilityChangedListener {
            override fun VisibilityChanged(isVisible: Boolean, id: Long) {
                method(isVisible,id)
            }
        }
    }

    interface OnLayerStateWasChangedListener {
        fun onSave(layerVisibility: LayerVisibility)
        fun onDelete(layerVisibility: LayerVisibility)
    }

    fun setOnLayerStateWasChangedListener(listener: OnLayerStateWasChangedListener) {
        saveListener = listener
    }

    fun setOnLayerStateWasChangedListener(
        method: (layerVisibility: LayerVisibility, type: String) -> Unit
    ) {
        saveListener = object :
            OnLayerStateWasChangedListener {

            override fun onSave(layerVisibility: LayerVisibility) {
                method(layerVisibility, "Save")
            }

            override fun onDelete(layerVisibility: LayerVisibility) {
                method(layerVisibility, "Delete")
            }
        }
    }

    interface onThemeChangeListener {
        fun onThemeChanged(layerId: Long, themeId: Long)
    }

    fun setOnThemeChangeListener(
        method: (layer: Long, theme: Long) -> Unit
    ) {
        themeListener = object :
            onThemeChangeListener {
            override fun onThemeChanged(layerId: Long, themeId: Long) {
                method(layerId, themeId)
            }
        }
    }

    //endregion

}