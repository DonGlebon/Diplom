package com.diplom.map.mvp.components.layervisibility.presenter

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layervisibility.contract.LayerVisibilityFragmentContract
import com.diplom.map.mvp.components.layervisibility.model.LayerVisibilityModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File


class LayerVisibilityFragmentPresenter : BasePresenter<LayerVisibilityFragmentContract.View>(),
    LayerVisibilityFragmentContract.Presenter {

    private val disposable = CompositeDisposable()
    private val model = LayerVisibilityModel()

    fun onRecyclerIsReady() {
        displayLayers()
    }

    private fun displayLayers() {
        disposable.add(
            model.getLayers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { view!!.addLayerToRecycler(it) }
                .doOnError {}
                .subscribe()
        )
    }

    override fun addNewLayer(file: File, activity: Context, norm: Boolean) {
        var filename = ""
        var filepath = ""
        val s = Environment.getExternalStorageDirectory()
        val a = Environment.getDataDirectory()
        val a1 = Environment.getDownloadCacheDirectory()
        val a2 = activity.cacheDir
        val a3 = activity.externalCacheDir
        val aa = activity.getExternalFilesDirs(null)
        if (!norm) {
            val path = file.absoluteFile.absolutePath
            if (path.substring(path.lastIndexOf("."), path.length) != ".shp") {
                Toast.makeText(this.view!!.getContext(), "Неверный формат файла", Toast.LENGTH_LONG).show()
                return
            }
            filename = path.substring(path.lastIndexOf('/') + 1).substringBefore('.')
            val uriPath = path.substringBefore(':')
            val pp = path.substring(path.lastIndexOf(':') + 1, path.lastIndexOf('/'))
            filepath = if (uriPath.toLowerCase().contains("primary"))
                "/storage/emulated/0/$pp/"
            else
                "${Environment.getExternalStorageDirectory()}/"
        } else {
            filepath = file.path.substring(0, file.path.lastIndexOf("/")) + java.io.File.separator
            filename = file.name.substring(0, file.name.lastIndexOf("."))
        }

        displayAddLayerDialog(activity, filename, filepath)
    }

    private fun displayAddLayerDialog(context: Context, filename: String, filepath: String) {
        AlertDialog.Builder(context)
            .setNegativeButton("Не добавлять") { _, _ -> }
            .setPositiveButton("Добавить") { _, _ -> addLayer(filename, filepath) }
            .setTitle("Добавить новый слой?")
            .create()
            .show()
    }

    private fun addLayer(filename: String, filepath: String) {
        disposable.add(
            model.addLayer(filename, filepath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view!!.displayProgressBar(true) }
                .doOnSuccess { view!!.displayProgressBar(false) }
                .doOnError {}
                .subscribe()
        )
    }

}