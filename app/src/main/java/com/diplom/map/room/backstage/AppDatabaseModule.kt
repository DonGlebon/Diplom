package com.diplom.map.room.backstage

import android.content.Context
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.diplom.map.room.AppDatabase
import com.diplom.map.room.entities.MainBase
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
                    MainBase.getValues(context)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess {
                            for (value in it)
                                db.insert("MainBase", OnConflictStrategy.IGNORE, value)
                        }
                        .subscribe()
                }
            })
            .fallbackToDestructiveMigration().build()

}