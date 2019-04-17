package com.diplom.map.mvp.components.fragments.layervisibility.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.fragments.layervisibility.contract.LayerVisibilityFragmentContract
import com.diplom.map.mvp.components.fragments.layervisibility.presenter.LayerVisibilityFragmentPresenter
import com.diplom.map.mvp.components.layerscreen.model.LayerRecyclerViewAdapter
import com.diplom.map.room.entities.LayerVisibility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import javax.inject.Inject


class LayerVisibilityFragment : BaseFragment(), LayerVisibilityFragmentContract.View {

    @Inject
    lateinit var presenter: LayerVisibilityFragmentPresenter

    private lateinit var progressBar: ProgressBar
    private lateinit var recycler: RecyclerView
    private lateinit var fab: FloatingActionButton
    private val layerList = ArrayList<LayerVisibility>()
    private lateinit var adapter: LayerRecyclerViewAdapter

    override fun init(savedInstanceState: Bundle?) {
        adapter = LayerRecyclerViewAdapter(layerList, this.context)
        App.get().injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter.attach(this)
        val rootView = inflater.inflate(R.layout.fragment_page_layers_visibility, container, false)
        recycler = rootView.findViewById(R.id.recyclerView)
        progressBar = rootView.findViewById(R.id.progressBar)
        fab = rootView.findViewById(R.id.fab)
        prepareRecycler()
        return rootView
    }

    private fun prepareRecycler() {
        recycler.layoutManager = LinearLayoutManager(this.activity)
        recycler.adapter = adapter
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                    fab.hide()
                else if (dy < 0)
                    fab.show()
            }
        })
        fab.setOnClickListener { openFileManager() }
        presenter.onRecyclerIsReady()
    }

    override fun displayProgressBar(display: Boolean) {
        progressBar.visibility = if (display) View.VISIBLE else View.INVISIBLE
    }

    override fun addLayerToRecycler(layers: List<LayerVisibility>) {
        displayProgressBar(true)
        layerList.clear()
        layerList.addAll(layers)
        adapter.notifyDataSetChanged()
        displayProgressBar(false)
    }

    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, FILE_MANAGER_REQUEST_CODE)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        resultData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == FILE_MANAGER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri?
            if (resultData != null) {
                uri = resultData.data
                val path = uri.path
                presenter.addNewLayer(File(path))
            }
        }
    }

    companion object {
        private const val FILE_MANAGER_REQUEST_CODE = 20
    }

}