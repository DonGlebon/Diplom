package com.diplom.map.mvp.components.layerscreen.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.config.room.AppDatabase
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LayerActivity : AppCompatActivity() {

    @Inject
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layer)
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        App.get().dbInjector.inject(this)
        CompositeDisposable().add(
            Single.just(db)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    //     it.multiPolygonDao().insert(MultiPolygon(4))
                    for (i in it.multiPolygonDao().getAllMultiPolygons())
                        Log.d("Hello", "room: ${i.uid}")
                }, { error -> Log.d("Hello", "error") })
        )
    }
}
