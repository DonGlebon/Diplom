package com.diplom.map.mvp.components.mapscreen.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView
import com.diplom.map.esri.model.ESRITileProvider

interface MapScreenContract {
    interface Presenter : BaseMvpPresenter<MapScreenContract.View> {
    }

    interface View : BaseView {
        fun addTileProvider(provider: ESRITileProvider)
    }
}