package com.diplom.map.mvp.components.layerscreen.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseCompatActivity
import com.diplom.map.mvp.components.layerscreen.contract.LayerScreenContract
import com.diplom.map.mvp.components.layerscreen.model.LayerRecyclerViewAdapter
import com.diplom.map.mvp.components.layerscreen.presenter.LayerScreenPresenter
import com.diplom.map.room.entities.LayerVisibility
import kotlinx.android.synthetic.main.activity_layer.*
import java.io.File
import javax.inject.Inject


class LayerActivity : BaseCompatActivity(), LayerScreenContract.View {

    @Inject
    lateinit var presenter: LayerScreenPresenter

    private var layerList = ArrayList<LayerVisibility>()
    private val adapter = LayerRecyclerViewAdapter(layerList, this)

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_layer)
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        App.get().injector.inject(this)
        presenter.attach(this)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        presenter.onRecyclerIsReady()
    }

    override fun addLayerToRecycler(layers: List<LayerVisibility>) {
        layerList.clear()
        layerList.addAll(layers)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_layers_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action__add_layer -> {
            openFileManager()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, FILE_MANAGER_REQUEST_CODE)
    }

    public override fun onActivityResult(
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

    override fun displayProgressBar(display: Boolean) {
        if (display)
            progressBar.visibility = ProgressBar.VISIBLE
        else progressBar.visibility = ProgressBar.INVISIBLE
    }

    companion object {
        private const val FILE_MANAGER_REQUEST_CODE = 20
    }

}
