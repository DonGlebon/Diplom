package com.diplom.map.mvp.abstracts.presenter

import com.diplom.map.mvp.abstracts.view.BaseView

interface BaseMvpPresenter<V : BaseView> {
    var isAttached: Boolean
    fun attach(view: V)
    fun detach()
}