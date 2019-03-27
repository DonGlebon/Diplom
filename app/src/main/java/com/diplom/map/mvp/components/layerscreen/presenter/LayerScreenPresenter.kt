package com.diplom.map.mvp.components.layerscreen.presenter

import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layerscreen.contract.LayerScreenContract
import com.diplom.map.room.AppDatabase
import javax.inject.Inject

class LayerScreenPresenter : BasePresenter<LayerScreenContract.View>(), LayerScreenContract.Presenter {

    init {
        App.get().dbInjector.inject(this)
    }

    @Inject
    lateinit var db: AppDatabase
}