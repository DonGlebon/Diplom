package com.diplom.map.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diplom.map.room.dao.LayerDao
import com.diplom.map.room.dao.MultiPolygonDao
import com.diplom.map.room.dao.PointDao
import com.diplom.map.room.dao.PolygonDao
import com.diplom.map.room.entities.Layer
import com.diplom.map.room.entities.MultiPolygon
import com.diplom.map.room.entities.Point
import com.diplom.map.room.entities.Polygon


@Database(entities = [Layer::class, MultiPolygon::class, Polygon::class, Point::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun layerDao(): LayerDao
    abstract fun multiPolygonDao(): MultiPolygonDao
    abstract fun polygonDao(): PolygonDao
    abstract fun pointDao(): PointDao
}