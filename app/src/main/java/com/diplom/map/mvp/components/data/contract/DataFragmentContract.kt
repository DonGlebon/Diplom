package com.diplom.map.mvp.components.data.contract

import com.diplom.map.mvp.abstracts.presenter.BaseMvpPresenter
import com.diplom.map.mvp.abstracts.view.BaseView

class DataFragmentContract {

    interface Presenter : BaseMvpPresenter<View> {}
    interface View : BaseView {}

}