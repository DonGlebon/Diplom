package com.diplom.map.mvp.abstracts.view

import android.content.Context
import android.view.View

interface BaseView {

    fun showView(view: View, isShown: Boolean) {
        view.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    fun getContext(): Context
}