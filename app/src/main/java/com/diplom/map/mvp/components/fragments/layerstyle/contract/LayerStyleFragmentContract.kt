package com.diplom.map.mvp.components.fragments.layerstyle.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView

interface LayerStyleFragmentContract {

    interface Presenter : BaseMvpPresenter<LayerStyleFragmentContract.View>

    interface View : BaseView

}