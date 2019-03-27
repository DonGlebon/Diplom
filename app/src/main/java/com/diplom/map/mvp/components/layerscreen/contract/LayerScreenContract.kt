package com.diplom.map.mvp.components.layerscreen.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView

interface LayerScreenContract {

    interface Presenter : BaseMvpPresenter<LayerScreenContract.View> {

    }

    interface View : BaseView

}