package com.diplom.map.mvp.components.fragments.map.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView

interface MapFragmentContract {

    interface Presenter : BaseMvpPresenter<MapFragmentContract.View>

    interface View : BaseView
}