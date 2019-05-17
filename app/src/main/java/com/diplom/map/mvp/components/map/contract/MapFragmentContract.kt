package com.diplom.map.mvp.components.map.contract

import com.diplom.map.esri.model.ESRITileProvider
import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView
import com.diplom.map.room.data.FDData
import com.diplom.map.room.data.SubFeatureData
import com.diplom.map.room.entities.FeatureData
import io.reactivex.Single

interface MapFragmentContract {

    interface Presenter : BaseMvpPresenter<MapFragmentContract.View> {
        fun getLayerDataById(uid: Long): Single<List<FDData>>
        fun getFeatureById(uid: Long): Single<List<SubFeatureData>>
    }

    interface View : BaseView {
        fun addTileProvider(provider: ESRITileProvider)
    }
}