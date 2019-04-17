package com.diplom.map.mvp.components.fragments.layerstyle.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.fragments.layerstyle.contract.LayerStyleFragmentContract
import com.diplom.map.mvp.components.fragments.layerstyle.presenter.LayerStyleFragmentPresenter
import javax.inject.Inject

class LayerStyleFragment : BaseFragment(), LayerStyleFragmentContract.View {

    @Inject
    lateinit var presenter: LayerStyleFragmentPresenter

    override fun init(savedInstanceState: Bundle?) {
        App.get().injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_page_layers_styles, container, false)
        return rootView
    }

}