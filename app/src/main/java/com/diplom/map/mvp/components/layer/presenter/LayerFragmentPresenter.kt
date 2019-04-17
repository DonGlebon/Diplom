package com.diplom.map.mvp.components.layer.presenter

import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layer.contract.LayerFragmentContract
import com.diplom.map.room.AppDatabase
import javax.inject.Inject

class LayerFragmentPresenter : BasePresenter<LayerFragmentContract.View>(), LayerFragmentContract.Presenter {

    @Inject
    lateinit var db: AppDatabase

    init {
        App.get().injector.inject(this)
    }


}