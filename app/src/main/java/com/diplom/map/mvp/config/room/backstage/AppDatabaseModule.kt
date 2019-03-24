package com.diplom.map.mvp.config.room.backstage

import android.content.Context
import androidx.room.Room
import com.diplom.map.mvp.config.room.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppDatabaseModule(var context: Context) {

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "mydb").fallbackToDestructiveMigration().build()

}