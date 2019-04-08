package com.diplom.map.mvp.components.layerscreen.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView
import com.diplom.map.room.entities.Layer
import java.io.File

interface LayerScreenContract {

    interface Presenter : BaseMvpPresenter<LayerScreenContract.View> {
        fun addNewLayer(file: File)
    }

    interface View : BaseView {
        fun displayProgressBar(display: Boolean)
        fun addLayerToRecycler(layers: ArrayList<Layer>)
    }

}