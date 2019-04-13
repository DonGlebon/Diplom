package com.diplom.map.mvp.components.fragments.layer.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.fragments.layer.presenter.LayerFragmentPresenter
import com.diplom.map.mvp.components.layerscreen.model.LayerRecyclerViewAdapter
import com.diplom.map.room.entities.Layer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject


class LayerFragment : BaseFragment() {

    @Inject
    lateinit var presenter: LayerFragmentPresenter

    private lateinit var mRecyclerView: RecyclerView

    override fun init(savedInstanceState: Bundle?) {
        App.get().injector.inject(this)
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_page_layers, container, false)
        setupRecycler(rootView)
        return rootView
    }

    private fun setupRecycler(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.visibility == View.VISIBLE) {
                    fab.hide()
                } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                    fab.show()
                }
            }
        })
        mRecyclerView.setItemViewCacheSize(0)
        mRecyclerView.adapter = LayerRecyclerViewAdapter(
            listOf(
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1"),
                Layer(0, "1", "1")
            )
            , activity!!.applicationContext
        )
    }
}