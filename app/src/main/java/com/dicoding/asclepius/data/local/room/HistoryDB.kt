package com.dicoding.asclepius.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.model.HistoryEntity

@Database(
    entities = [HistoryEntity::class],
    version = 1,
    exportSchema = true
)
abstract class HistoryDB : RoomDatabase() {

    abstract fun historyDao() : Dao

    companion object{
        @Volatile
        private var INSTANCE: HistoryDB ?= null

        fun getInstance(context: Context): HistoryDB =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDB::class.java, "History.db"
                ).build()
            }
    }
}