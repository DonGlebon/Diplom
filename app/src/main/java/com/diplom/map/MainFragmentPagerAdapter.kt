package com.diplom.map

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.diplom.map.mvp.components.fragments.layer.view.LayerFragment
import com.diplom.map.mvp.components.fragments.map.view.MapFragment

class MainFragmentPagerAdapter(fm: FragmentManager, var context: Context) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MapFragment()
            1 -> LayerFragment()
            2 -> LayerFragment()
            else -> LayerFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}