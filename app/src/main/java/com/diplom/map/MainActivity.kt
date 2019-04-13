package com.diplom.map

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolBar))
        setupUI()
    }

    private fun setupUI() {
        findViewById<ViewPager>(R.id.viewPager).adapter = MainFragmentPagerAdapter(supportFragmentManager, this)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_map -> viewPager.currentItem = 0
                R.id.action_layer -> viewPager.currentItem = 1
                R.id.action_data -> viewPager.currentItem = 2
                else -> viewPager.currentItem = 0
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_main_menu, menu)
        return true
    }
}
