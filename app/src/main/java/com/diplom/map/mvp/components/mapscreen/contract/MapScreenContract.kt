package com.diplom.map.mvp.components.mapscreen.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView
import com.google.android.gms.maps.model.TileProvider

interface MapScreenContract {
    interface Presenter : BaseMvpPresenter<MapScreenContract.View> {
    }

    interface View : BaseView {
        fun addTileProvidersToMap(provider: TileProvider)
    }
}