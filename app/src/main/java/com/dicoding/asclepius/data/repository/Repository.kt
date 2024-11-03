package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.asclepius.BuildConfig.API_KEY
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.local.model.HistoryEntity
import com.dicoding.asclepius.data.local.room.Dao
import com.dicoding.asclepius.data.remote.model.ArticlesItem
import com.dicoding.asclepius.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository private constructor(
    private val api: ApiService,
    private val historyDao: Dao
){
    fun saveHistory(history : HistoryEntity) : LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            historyDao.insertHistory(history)
            emit(Result.Success(true))
        }catch (e:Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getHistory(): LiveData<Result<List<HistoryEntity>>> =
        historyDao.getHistory().map { history ->
            Result.Success(history)
        }

    suspend fun fetchNewsArticles(): List<ArticlesItem> {
        return withContext(Dispatchers.IO) {
            val response = api.getArticle(apiKey = API_KEY)
            if (response.articles.isNotEmpty()) {
                response.articles.filter {
                    !it.title.isNullOrEmpty() && !it.title.contains("[Removed]", ignoreCase = true)
                }
            } else {
                emptyList()
            }
        }
    }

    suspend fun deleteHistory(id: Int) {
        withContext(Dispatchers.IO) {
            historyDao.deleteHistory(id)
        }
    }


    companion object{

        private var INSTANCE: Repository ?= null

        fun getInstance(api: ApiService,historyDao: Dao):Repository =
            INSTANCE?: synchronized(this){
                INSTANCE ?: Repository(api,historyDao)
            }.also { INSTANCE = it }
    }
}