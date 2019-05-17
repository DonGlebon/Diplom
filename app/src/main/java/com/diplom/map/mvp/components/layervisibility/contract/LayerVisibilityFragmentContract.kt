package com.diplom.map.mvp.components.layervisibility.contract

import android.content.Context
import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView
import com.diplom.map.room.data.LayerVisibility
import java.io.File

interface LayerVisibilityFragmentContract {

    interface Presenter : BaseMvpPresenter<View> {
        fun addNewLayer(file: File, activity: Context, norm: Boolean = false)
    }

    interface View : BaseView {
        fun displayProgressBar(display: Boolean)
        fun addLayerToRecycler(layers: List<LayerVisibility>)
    }

}