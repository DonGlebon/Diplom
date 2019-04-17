package com.diplom.map.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diplom.map.room.dao.*
import com.diplom.map.room.entities.*


@Database(
    entities = [Layer::class,
        SubFeature::class,
        Feature::class,
        Point::class,
        FeatureData::class,
        FeatureStyle::class],
    version = 23,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun layerDao(): LayerDao
    abstract fun multiPolygonDao(): MultiPolygonDao
    abstract fun polygonDao(): PolygonDao
    abstract fun pointDao(): PointDao
    abstract fun globalDao(): GlobalDao
    abstract fun featureDataDao(): FeatureDataDao
    abstract fun featureStyleDao(): FeatureStyleDao
}