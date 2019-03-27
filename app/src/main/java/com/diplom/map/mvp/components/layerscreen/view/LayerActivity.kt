package com.diplom.map.mvp.components.layerscreen.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import com.bbn.openmap.dataAccess.shape.ShapeUtils
import com.bbn.openmap.layer.shape.ESRIPoly
import com.bbn.openmap.layer.shape.ESRIPolygonRecord
import com.bbn.openmap.layer.shape.ESRIRecord
import com.bbn.openmap.layer.shape.ShapeFile
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseCompatActivity
import com.diplom.map.mvp.components.layerscreen.contract.LayerScreenContract
import com.diplom.map.mvp.components.layerscreen.presenter.LayerScreenPresenter
import com.diplom.map.room.entities.Layer
import com.diplom.map.room.entities.MultiPolygon
import com.diplom.map.room.entities.Point
import com.diplom.map.room.entities.Polygon
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_layer.*
import java.io.File
import javax.inject.Inject


class LayerActivity : BaseCompatActivity(), LayerScreenContract.View {

    @Inject
    lateinit var presenter: LayerScreenPresenter

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_layer)
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        App.get().injector.inject(this)
        presenter.attach(this)
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
        if (requestCode == FILE_MANAGER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            progressBar.visibility = ProgressBar.VISIBLE
            addLayer("kvartal_zone", "/storage/emulated/0/Map/")
            val uri: Uri?
            if (resultData != null) {
                uri = resultData.data
            }
        }
    }

    private val disposable = CompositeDisposable()

    private fun addLayer(name: String, path: String) {
        disposable.add(presenter.db.layerDao().findLayerByName(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    addPolygons(it)
                },
                { Log.d("Hello", "Find layer error: ${it.message}") },
                {
                    insertLayerToDatabase(name, path)
                }
            )
        )
    }

    private fun insertLayerToDatabase(name: String, path: String) {
        disposable.add(presenter.db.layerDao().insert(Layer(0, name, path))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    addPolygons(it)
                },
                { Log.d("Hello", "Insert err") }
            )
        )
    }

    private fun addPolygons(lid: Long) {
        disposable.add(presenter.db.layerDao().getLayerById(lid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { layer ->
                    val shapePolygons = readShapeFile(layer.name, layer.path)
                    val multiPolygons = ArrayList<MultiPolygon>()
                    for (i in 0 until shapePolygons.size)
                        multiPolygons.add(MultiPolygon(i.toLong(), lid))
                    clearOldData(layer.uid, multiPolygons, shapePolygons)

                },
                {
                    Log.d("Hello", "Error 3 ${it.message}")
                }
            )
        )
    }

    private fun clearOldData(
        lid: Long,
        multiPolygons: ArrayList<MultiPolygon>,
        shapePolygons: ArrayList<MultiPolygonData>
    ) {
        disposable.add(presenter.db.multiPolygonDao().deleteAll(lid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe
            {
                insertMultiPolygonsToDatabase(multiPolygons, shapePolygons)
            }
        )
    }

    private fun insertMultiPolygonsToDatabase(
        multiPolygons: ArrayList<MultiPolygon>,
        shapePolygons: ArrayList<MultiPolygonData>
    ) {
        disposable.add(
            presenter.db.multiPolygonDao().insert(multiPolygons)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { multiPolygonList ->
                        val polygons = ArrayList<Polygon>()
                        for (i in 0 until shapePolygons.size) {
                            for (j in 0 until shapePolygons[i].polygons.size)
                                polygons.add(Polygon(0, multiPolygonList[i]))
                        }
                        insertPolygonsToDatabase(polygons, shapePolygons)
                    },
                    {
                        Log.d("Hello", "Error 4 ${it.message}")
                    }
                )
        )
    }

    private fun insertPolygonsToDatabase(
        polygons: ArrayList<Polygon>,
        shapePolygons: ArrayList<MultiPolygonData>
    ) {
        disposable.add(presenter.db.polygonDao().insert(polygons)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { polygonList ->
                    val points = ArrayList<Point>()
                    for (pol in shapePolygons)
                        for (i in 0 until pol.polygons.size)
                            for (point in pol.polygons[i].points)
                                points.add(
                                    Point(
                                        0,
                                        polygonList[i],
                                        point.latitude,
                                        point.longitude
                                    )
                                )
                    insertPointsToDatabase(points)

                },
                {
                    Log.d("Hello", "Error 5w ${it.message}")
                }
            )
        )
    }

    private fun insertPointsToDatabase(points: ArrayList<Point>) {
        disposable.add(
            presenter.db.pointDao().insert(points)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        progressBar.visibility = ProgressBar.INVISIBLE
                    },
                    {
                        Log.d(
                            "Hello",
                            "Error 6 ${it.message}"
                        )
                    }
                )
        )
    }

    private fun readShapeFile(name: String, path: String): ArrayList<MultiPolygonData> {
        val multiPolygons = ArrayList<MultiPolygonData>()
        try {
            val shapeFile = ShapeFile(File("${path + name}.shp"))
            var record: ESRIRecord? = shapeFile.nextRecord
            while (record != null) {
                if (ShapeUtils.getStringForType(record.shapeType) == "POLYGON") {
                    val multiPolygonRecord = record as ESRIPolygonRecord
                    val multiPolygon = MultiPolygonData()
                    for (i in multiPolygonRecord.polygons.indices) {
                        val polygonRecord = multiPolygonRecord.polygons[i] as ESRIPoly.ESRIFloatPoly
                        val polygon = PolygonData()
                        for (j in 0 until polygonRecord.nPoints) {
                            polygon.addPoint(LatLng(polygonRecord.getY(j), polygonRecord.getX(j)))
                        }
                        multiPolygon.addPolygon(polygon)
                    }
                    multiPolygons.add(multiPolygon)
                }
                record = shapeFile.nextRecord
            }
        } catch (e: Exception) {
            Log.d("Hello", "read err ${e.message}")
        }
        return multiPolygons
    }

    companion object {
        private const val FILE_MANAGER_REQUEST_CODE = 20
    }

    class MultiPolygonData {
        val polygons = ArrayList<PolygonData>()
        fun addPolygon(polygon: PolygonData) {
            polygons.add(polygon)
        }

    }

    class PolygonData {
        val points = ArrayList<LatLng>()
        fun addPoint(point: LatLng) {
            points.add(point)
        }
    }
}
