package com.diplom.map.mvp.config.di

import com.diplom.map.MainActivity
import com.diplom.map.location.LocationProviderModule
import com.diplom.map.mvp.components.data.backstage.DataFragmentModule
import com.diplom.map.mvp.components.data.presenter.DataFragmentPresenter
import com.diplom.map.mvp.components.data.view.DataFragment
import com.diplom.map.mvp.components.layer.backstage.LayerFragmentModule
import com.diplom.map.mvp.components.layer.presenter.LayerFragmentPresenter
import com.diplom.map.mvp.components.layer.view.LayerFragment
import com.diplom.map.mvp.components.layerstyle.backstage.LayerStyleFragmentModule
import com.diplom.map.mvp.components.layerstyle.model.StyleRecyclerViewAdapter
import com.diplom.map.mvp.components.layerstyle.presenter.LayerStyleFragmentPresenter
import com.diplom.map.mvp.components.layerstyle.view.LayerStyleFragment
import com.diplom.map.mvp.components.layervisibility.backstage.LayerVisibilityFragmentModule
import com.diplom.map.mvp.components.layervisibility.model.LayerRecyclerViewAdapter
import com.diplom.map.mvp.components.layervisibility.presenter.LayerVisibilityFragmentPresenter
import com.diplom.map.mvp.components.layervisibility.view.LayerVisibilityFragment
import com.diplom.map.mvp.components.map.backstage.MapFragmentModule
import com.diplom.map.mvp.components.map.presenter.MapFragmentPresenter
import com.diplom.map.mvp.components.map.view.MapFragment
import com.diplom.map.mvp.config.retrofit.GeoserverClientModule
import com.diplom.map.room.backstage.AppDatabaseModule
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppDatabaseModule::class,
        MapFragmentModule::class,
        DataFragmentModule::class,
        LayerFragmentModule::class,
        LayerStyleFragmentModule::class,
        LocationProviderModule::class,
        GeoserverClientModule::class,
        LayerVisibilityFragmentModule::class]
)
@Singleton
interface AppDiComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(layerFragment: LayerFragment)
    fun inject(layerFragmentPresenter: LayerFragmentPresenter)

    fun inject(layerStyleFragment: LayerStyleFragment)
    fun inject(layerStyleFragmentPresenter: LayerStyleFragmentPresenter)

    fun inject(layerRecyclerViewAdapter: LayerRecyclerViewAdapter)
    fun inject(styleRecyclerViewAdapter: StyleRecyclerViewAdapter)

    fun inject(mapFragment: MapFragment)
    fun inject(mapFragmentPresenter: MapFragmentPresenter)

    fun inject(layerVisibilityFragment: LayerVisibilityFragment)
    fun inject(layerVisibilityFragmentPresenter: LayerVisibilityFragmentPresenter)

    fun inject(dataFragment: DataFragment)
    fun imject(dataFragmentPresenter: DataFragmentPresenter)
}