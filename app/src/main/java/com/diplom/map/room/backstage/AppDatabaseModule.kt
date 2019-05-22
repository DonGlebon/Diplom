package com.diplom.map.room.backstage

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.diplom.map.room.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppDatabaseModule(var context: Context) {

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "mydb")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                }
            })
            .fallbackToDestructiveMigration().build()

}