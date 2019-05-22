package com.diplom.map.mvp.components.layervisibility.presenter

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.diplom.map.utils.ShapeFileUtils
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.presenter.BasePresenter
import com.diplom.map.mvp.components.layervisibility.contract.LayerVisibilityFragmentContract
import com.diplom.map.room.AppDatabase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject


class LayerVisibilityFragmentPresenter : BasePresenter<LayerVisibilityFragmentContract.View>(),
    LayerVisibilityFragmentContract.Presenter {

    @Inject
    lateinit var db: AppDatabase

    private val disposable = CompositeDisposable()

    init {
        App.get().injector.inject(this)
    }

    fun onRecyclerIsReady() {
        displayLayers()
    }

    private fun displayLayers() {
        disposable.add(db.layerDao().getLayers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { view!!.addLayerToRecycler(it) }
            .doOnError { Log.d("Hello", "Get layers error: ${it.message}") }
            .subscribe()
        )
    }

    override fun addNewLayer(file: File, activity: Context, norm: Boolean) {
        var filename = ""
        var filepath = ""
        if (!norm) {
            val path = file.absoluteFile.absolutePath
            filename = path.substring(path.lastIndexOf('/') + 1).substringBefore('.')
            val uriPath = path.substringBefore(':')
            val pp = path.substring(path.lastIndexOf(':') + 1, path.lastIndexOf('/'))
            filepath = if (uriPath.toLowerCase().contains("primary"))
                "/storage/emulated/0/$pp/"
            else
                "${Environment.getExternalStorageDirectory()}/"
        } else {
            filepath = file.path.substring(0, file.path.lastIndexOf("/")) + java.io.File.separator
            filename = file.name.substring(0,file.name.lastIndexOf("."))
        }

        AlertDialog.Builder(activity)
            .setNegativeButton("Не добавлять") { _, _ -> }
            .setPositiveButton("Добавить") { _, _ ->
                view!!.displayProgressBar(true)
                addLayer(filename, filepath)
            }
            .setTitle("Добавить новый слой?")
            .create()
            .show()
    }

    private fun insertLayer(filename: String, filepath: String): Boolean {
        db.globalDao()
            .insertShapeFileData(filename, filepath, ShapeFileUtils.readShapeFile(filename, filepath))
        return true
    }

    private fun addLayer(filename: String, filepath: String) {
        disposable.add(Single.defer { Single.just(insertLayer(filename, filepath)) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view!!.displayProgressBar(true) }
            .doOnSuccess { view!!.displayProgressBar(false) }
            .doOnError { Log.d("Hello", "Error: ${it.message}") }
            .subscribe())
    }

}