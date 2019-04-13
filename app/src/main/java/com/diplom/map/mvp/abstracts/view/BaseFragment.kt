package com.diplom.map.mvp.abstracts.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(), BaseView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
    }

    protected abstract fun init(savedInstanceState: Bundle?)

    override fun getContext(): Context = context
}