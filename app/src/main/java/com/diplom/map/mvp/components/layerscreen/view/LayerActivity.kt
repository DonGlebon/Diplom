package com.diplom.map.mvp.components.layerscreen.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.diplom.map.R

class LayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layer)
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
