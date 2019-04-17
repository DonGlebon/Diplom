package com.diplom.map.mvp.config.di

import com.diplom.map.mvp.components.fragments.layer.backstage.LayerFragmentModule
import com.diplom.map.mvp.components.fragments.layer.presenter.LayerFragmentPresenter
import com.diplom.map.mvp.components.fragments.layer.view.LayerFragment
import com.diplom.map.mvp.components.fragments.layerstyle.backstage.LayerStyleFragmentModule
import com.diplom.map.mvp.components.fragments.layerstyle.presenter.LayerStyleFragmentPresenter
import com.diplom.map.mvp.components.fragments.layerstyle.view.LayerStyleFragment
import com.diplom.map.mvp.components.fragments.layervisibility.backstage.LayerVisibilityFragmentModule
import com.diplom.map.mvp.components.fragments.layervisibility.presenter.LayerVisibilityFragmentPresenter
import com.diplom.map.mvp.components.fragments.layervisibility.view.LayerVisibilityFragment
import com.diplom.map.mvp.components.fragments.map.backstage.MapFragmentModule
import com.diplom.map.mvp.components.fragments.map.presenter.MapFragmentPresenter
import com.diplom.map.mvp.components.fragments.map.view.MapFragment
import com.diplom.map.mvp.components.layerscreen.backstage.LayerScreenModule
import com.diplom.map.mvp.components.layerscreen.model.LayerRecyclerViewAdapter
import com.diplom.map.mvp.components.layerscreen.presenter.LayerScreenPresenter
import com.diplom.map.mvp.components.layerscreen.view.LayerActivity
import com.diplom.map.mvp.components.mapscreen.backstage.MapScreenModule
import com.diplom.map.mvp.components.mapscreen.presenter.MapScreenPresenter
import com.diplom.map.mvp.components.mapscreen.view.MapActivity
import com.diplom.map.room.backstage.AppDatabaseModule
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [MapScreenModule::class,
        LayerScreenModule::class,
        AppDatabaseModule::class,
        RxModule::class,
        MapFragmentModule::class,
        LayerFragmentModule::class,
        LayerStyleFragmentModule::class,
        LayerVisibilityFragmentModule::class]
)
@Singleton
interface AppDiComponent {

    fun inject(mapScreenActivity: MapActivity)
    fun inject(mapScreenPresenter: MapScreenPresenter)

    fun inject(layerScreenActivity: LayerActivity)
    fun inject(layerScreenPresenter: LayerScreenPresenter)

    fun inject(layerFragment: LayerFragment)
    fun inject(layerFragmentPresenter: LayerFragmentPresenter)

    fun inject(layerStyleFragment: LayerStyleFragment)
    fun inject(layerStyleFragmentPresenter: LayerStyleFragmentPresenter)

    fun inject(layerRecyclerViewAdapter: LayerRecyclerViewAdapter)

    fun inject(mapFragment: MapFragment)
    fun inject(mapFragmentPresenter: MapFragmentPresenter)

    fun inject(layerVisibilityFragment: LayerVisibilityFragment)
    fun inject(layerVisibilityFragmentPresenter: LayerVisibilityFragmentPresenter)

}