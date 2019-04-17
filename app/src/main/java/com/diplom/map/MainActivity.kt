package com.diplom.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.diplom.map.mvp.components.fragments.layer.view.LayerFragment
import com.diplom.map.mvp.components.fragments.map.view.MapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolBar))
        setupPermissions()
        setupUI()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        }
    }

    private fun setupUI() {
        val active: Fragment = when (bottomNavigationView.selectedItemId) {
            R.id.action_map -> MapFragment()
            R.id.action_layer -> LayerFragment()
            R.id.action_data -> LayerFragment()
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
                R.id.action_map -> transaction.replace(R.id.fragmentContainer, MapFragment())
                R.id.action_layer -> transaction.replace(R.id.fragmentContainer, LayerFragment())
                R.id.action_data -> transaction.replace(R.id.fragmentContainer, LayerFragment())
                else -> transaction.replace(R.id.fragmentContainer, MapFragment())
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            transaction.commit()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_main_menu, menu)
        return true
    }
}
