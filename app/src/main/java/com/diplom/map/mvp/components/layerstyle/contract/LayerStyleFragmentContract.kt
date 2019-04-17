package com.diplom.map.mvp.components.layerstyle.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView

interface LayerStyleFragmentContract {

    interface Presenter : BaseMvpPresenter<View>

    interface View : BaseView

}