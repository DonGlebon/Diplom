package com.diplom.map.mvp.components.layervisibility.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView
import com.diplom.map.room.entities.LayerVisibility
import java.io.File

interface LayerVisibilityFragmentContract {

    interface Presenter : BaseMvpPresenter<View> {
        fun addNewLayer(file: File)
    }

    interface View : BaseView {
        fun displayProgressBar(display: Boolean)
        fun addLayerToRecycler(layers: List<LayerVisibility>)
    }

}