package com.diplom.map.mvp.components.mapscreen.contract

import com.diplom.map.GeoPolyLayer
import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView
import com.google.android.gms.maps.GoogleMap

interface MapScreenContract {
    interface Presenter : BaseMvpPresenter<MapScreenContract.View> {
        fun getLayout(map: GoogleMap): GeoPolyLayer
    }

    interface View : BaseView
}