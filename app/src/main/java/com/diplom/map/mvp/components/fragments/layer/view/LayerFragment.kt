package com.diplom.map.mvp.components.fragments.layer.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.fragments.layer.contract.LayerFragmentContract
import com.diplom.map.mvp.components.fragments.layer.model.LayerFragmentViewPagerAdapter
import com.diplom.map.mvp.components.fragments.layer.presenter.LayerFragmentPresenter
import kotlinx.android.synthetic.main.fragment_page_layers.view.*
import javax.inject.Inject


class LayerFragment : BaseFragment(), LayerFragmentContract.View {

    @Inject
    lateinit var presenter: LayerFragmentPresenter

    override fun init(savedInstanceState: Bundle?) {
        App.get().injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_page_layers, container, false)
        setupUI(rootView)
        presenter.attach(this)
        return rootView
    }

    private fun setupUI(view: View) {
        view.layerPager.adapter = LayerFragmentViewPagerAdapter(this.fragmentManager!!, this.context)
        view.tabLayout.setupWithViewPager(view.layerPager)
    }

}