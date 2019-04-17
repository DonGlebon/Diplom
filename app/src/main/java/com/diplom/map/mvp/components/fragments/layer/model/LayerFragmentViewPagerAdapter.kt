package com.diplom.map.mvp.components.fragments.layer.model

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.diplom.map.mvp.components.fragments.layerstyle.view.LayerStyleFragment
import com.diplom.map.mvp.components.fragments.layervisibility.view.LayerVisibilityFragment

class LayerFragmentViewPagerAdapter(fm: FragmentManager, var context: Context) : FragmentStatePagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence? {
        return if(position == 0) "Слои" else "Стили"
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> LayerVisibilityFragment()
            1 -> LayerStyleFragment()
            else -> LayerStyleFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}