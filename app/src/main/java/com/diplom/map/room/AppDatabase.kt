package com.diplom.map.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diplom.map.room.dao.FeatureDataDao
import com.diplom.map.room.dao.FeatureStyleDao
import com.diplom.map.room.dao.GlobalDao
import com.diplom.map.room.dao.LayerDao
import com.diplom.map.room.entities.*


@Database(
    entities = [Layer::class,
        SubFeature::class,
        MainBase::class,
        Feature::class,
        ThemeStyle::class,
        ThemeStyleValues::class,
        Marker::class,
        Point::class,
        FeatureData::class],
    version = 5123,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun layerDao(): LayerDao
    abstract fun globalDao(): GlobalDao
    abstract fun featureDataDao(): FeatureDataDao
    abstract fun featureStyleDao(): FeatureStyleDao
}