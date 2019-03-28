package com.diplom.map.mvp.components.layerscreen.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView
import java.io.File

interface LayerScreenContract {

    interface Presenter : BaseMvpPresenter<LayerScreenContract.View> {
        fun addLayer(file: File)
    }

    interface View : BaseView {
        fun displayProgressBar(display: Boolean)
    }

}