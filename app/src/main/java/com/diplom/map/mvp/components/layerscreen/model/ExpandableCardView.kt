package com.diplom.map.mvp.components.layerscreen.model

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.LinearLayout
import com.diplom.map.R
import kotlinx.android.synthetic.main.cardview_body.view.*
import kotlinx.android.synthetic.main.expandable_cardview.view.*

class ExpandableCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {


    private val ANIMATION_DURATION = 350L
    private var title: String? = null
    private var innerViewID: Int = 0
    var innerView: View? = null

    var isExpanded: Boolean = true
        private set

    private var headerHeight: Int = 0
    private var bodyHeight: Int = 0

    private var isMoving = false

    private val expandClickListener = OnClickListener {
        if (!isMoving) {
            animateExpanding()
            animateArrow()
            isExpanded = !isExpanded
        }
    }

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

    private fun collapse() {
        card_layout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, headerHeight)
    }

    private fun expand() {
        card_layout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, headerHeight + bodyHeight)
    }

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

    fun setTitle(headerTitle: String?) {
        if (headerTitle != null) {
            card_title.text = headerTitle
        }
    }

    private fun updateBodyHeight() {
        innerView!!.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        bodyHeight = innerView!!.measuredHeight
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        card_layout.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        headerHeight = card_layout.measuredHeight
        setInnerView(innerViewID)
        card_layout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, headerHeight)
        card_header.setOnClickListener(expandClickListener)
        expandButton.setOnClickListener(expandClickListener)
        removeLayerButton.setOnClickListener {
            AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle).apply {
                setTitle("Удаление слоя")
                setMessage("Удалить слой %LayerName%?")
                setPositiveButton("Удалить", { dialog, which -> })
                setNegativeButton("Отменить", { dialog, which -> })
            }.create().show()
        }
    }

    private fun setInnerView(innerViewID: Int) {
        val container = this.findViewById<LinearLayout>(R.id.card_layout_container)
        innerView = LayoutInflater.from(context).inflate(innerViewID, this, false)
        container.addView(innerView)
        updateBodyHeight()
    }


}