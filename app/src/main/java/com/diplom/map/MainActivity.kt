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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import okhttp3.ResponseBody
import java.io.*
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
            //R.id.action_data -> LayerFragment()
            else -> LayerFragment()
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
                //R.id.action_data -> transaction.replace(R.id.fragmentContainer, DataFragment())
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
        toolbar?.setOnClickListener {
            Log.d("Hello", "Click")
            geoserverClient.getApi()
                .getLayerNames("http://nuolh.belstu.by:4201/geoserver/rest/workspaces/myws/featuretypes.json")
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
                            // Toast.makeText(this,,Toast.LENGTH_SHORT).show()
                            val shapeName = it[selectedPosition].toLowerCase()
                            val shapeFileUrl =
                                "http://nuolh.belstu.by:4201/geoserver/cite/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cite%3A$shapeName&maxFeatures=5000000&outputFormat=SHAPE-ZIP&srsName=EPSG:4326&format_options=CHARSET%3AUTF-8"
                            Log.d("Hello", "path: $shapeFileUrl")
                            geoserverClient.getApi().getShapeFile(shapeFileUrl)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess { body ->
                                    val zip = writeResponseBodyToDisk(shapeName, body)
                                    val directory = zip?.path?.substring(0, zip.path!!.lastIndexOf("."))
                                    if (zip != null) {
                                        unzip(zip, File(directory))
                                        val dir =
                                            getExternalFilesDir(null)?.path + separator + shapeName + separator + shapeName + ".shp"
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
                        .create()
                        .show()
                }
                .doOnError { Log.d("Hello", "Error: ${it.message}") }
                .subscribe()
        }
        return true
    }


    private fun writeResponseBodyToDisk(filename: String, body: ResponseBody): File? {
        try {
            val futureStudioIconFile = File(getExternalFilesDir(null)?.path + separator + "$filename.zip")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()

                return futureStudioIconFile
            } catch (e: IOException) {
                return null
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            return null
        }

    }

    @Throws(IOException::class)
    fun unzip(archive: File, targetDirectory: File) {
        val source = archive.path
        val destination = targetDirectory.path
        try {
            val zipFile = ZipFile(source)
            zipFile.extractAll(destination)
        } catch (e: ZipException) {
            e.printStackTrace()
        }
    }

}





