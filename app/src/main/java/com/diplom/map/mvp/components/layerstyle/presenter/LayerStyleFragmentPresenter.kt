package com.diplom.map.mvp.components.layerstyle.presenter

import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layerstyle.contract.LayerStyleFragmentContract
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.data.ThemeStyleData
import io.reactivex.Maybe
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

    fun getLayers(): Maybe<List<String>> {
        return db.layerDao().getLayerNames()
    }

    fun getColumnNames(layername: String): Maybe<List<String>> {
        return db.layerDao().getColumnNamesByLayer(layername)
    }

    fun getColumnValues() {
        db.layerDao()
    }

    fun getThemeValues(layername: String, columnname: String): Maybe<ThemeStyleData> {
        return db.layerDao().getThemeValues(layername, columnname)
    }

}