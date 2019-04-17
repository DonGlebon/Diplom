package com.diplom.map.mvp.components.layer.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView

interface LayerFragmentContract {

    interface Presenter : BaseMvpPresenter<View> {}

    interface View : BaseView {}

}