package com.diplom.map.mvp.components.layerstyle.presenter

import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layerstyle.contract.LayerStyleFragmentContract
import com.diplom.map.room.AppDatabase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LayerStyleFragmentPresenter : BasePresenter<LayerStyleFragmentContract.View>(),
    LayerStyleFragmentContract.Presenter {

    @Inject
    lateinit var db: AppDatabase

    val disposable = CompositeDisposable()

    init {
        App.get().injector.inject(this)
    }

}