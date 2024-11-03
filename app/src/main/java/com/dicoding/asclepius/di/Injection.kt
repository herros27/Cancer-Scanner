package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.local.room.HistoryDB
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.data.repository.Repository

object Injection {
    fun provideRepo(context: Context):Repository{
        val db = HistoryDB.getInstance(context)
        val dao = db.historyDao()
        val api = ApiConfig.apiService
        return Repository.getInstance(api,dao)
    }
}