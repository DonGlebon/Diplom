package com.diplom.map.mvp.components.fragments.layerstyle.presenter

import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.fragments.layerstyle.contract.LayerStyleFragmentContract
import com.diplom.map.room.AppDatabase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LayerStyleFragmentPresenter : BasePresenter<LayerStyleFragmentContract.View>(),
    LayerStyleFragmentContract.Presenter {

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var disposable: CompositeDisposable

    init {
        App.get().injector.inject(this)
    }

}