package com.diplom.map.room.dao

import androidx.room.Dao
import androidx.room.Transaction

@Dao
interface GlobalDao {



    @Transaction
    fun insertShapeFileData() {

    }
}