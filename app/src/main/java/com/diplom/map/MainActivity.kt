package com.diplom.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.diplom.map.mvp.App
import com.diplom.map.mvp.components.layer.view.LayerFragment
import com.diplom.map.mvp.components.layervisibility.presenter.LayerVisibilityFragmentPresenter
import com.diplom.map.mvp.components.map.view.MapFragment
import com.diplom.map.mvp.config.retrofit.GeoserverClient
import com.diplom.map.utils.FileUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.File.separator
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var geoserverClient: GeoserverClient

    @Inject
    lateinit var layerPresenter: LayerVisibilityFragmentPresenter

    var toolbar: Toolbar? = null
    var server: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.get().injector.inject(this)
        toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)
        setupPermissions()
        setupUI()
    }

    private fun setupPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                102
            )
        }
    }

    private fun setupUI() {
        val active: Fragment = when (bottomNavigationView.selectedItemId) {
            R.id.action_map -> MapFragment()
            R.id.action_layer -> LayerFragment()
            else -> MapFragment()
        }
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.fragmentContainer, active)
            .commit()
        bottomNavigationView.setOnNavigationItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.action_map -> {
                    transaction.replace(R.id.fragmentContainer, MapFragment());server?.isVisible = false
                }
                R.id.action_layer -> {
                    transaction.replace(R.id.fragmentContainer, LayerFragment());server?.isVisible = true
                }
                else -> transaction.replace(R.id.fragmentContainer, MapFragment())
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            transaction.commit()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_main_menu, menu)
        server = menu?.findItem(R.id.getLayers)
        server?.isVisible = false
        server?.setOnMenuItemClickListener {
            getLayerNamesFromServer()
            return@setOnMenuItemClickListener true
        }

        return true
    }


    fun getLayerNamesFromServer() {
        geoserverClient.getApi()
            .getLayerNames()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                val list = ArrayList<String>()
                for (layer in it.featureTypes?.featureType!!)
                    list.add(layer.name)
                list
            }
            .doOnSuccess {
                AlertDialog.Builder(this)
                    .setTitle("Слой")
                    .setSingleChoiceItems(it.toTypedArray(), 0, null)
                    .setNegativeButton("Отменить") { _, _ -> }
                    .setPositiveButton("Добавить") { dialog, _ ->
                        val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
                        val shapeName = it[selectedPosition].toLowerCase()
                        downloadShapefileFromServer(shapeName)
                    }
                    .create()
                    .show()
            }
            .doOnError { Log.d("Hello", "Error: ${it.message}") }
            .subscribe()
    }

    fun downloadShapefileFromServer(shapefileName: String) {
        geoserverClient.getApi().getShapeFile("cite%3A$shapefileName")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { body ->
                val zip = FileUtils.writeResponseBodyToDisk(shapefileName, body, this)
                val directory = zip?.path?.substring(0, zip.path!!.lastIndexOf("."))
                if (zip != null) {
                    FileUtils.unzip(zip, File(directory))
                    val dir =
                        getExternalFilesDir(null)?.path + "$separator$shapefileName$separator$shapefileName.shp"
                    layerPresenter.addNewLayer(File(dir), this, true)

                } else
                    Toast.makeText(
                        this,
                        "Не удалось скачать файл с сервера",
                        Toast.LENGTH_LONG
                    ).show()
            }
            .doOnError {
                Log.d("Hello", "Error: ${it.message}")
            }

            .subscribe()
    }

}





