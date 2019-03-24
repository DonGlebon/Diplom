package com.diplom.map.mvp.config.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [MultiPolygon::class, Polygon::class, Point::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun multiPolygonDao(): MultiPolygonDao
    abstract fun polygonDao(): PolygonDao
    abstract fun pointDao(): PointDao
}