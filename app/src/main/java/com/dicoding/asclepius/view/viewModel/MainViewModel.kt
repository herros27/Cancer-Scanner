package com.dicoding.asclepius.view.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.local.model.HistoryEntity
import com.dicoding.asclepius.data.remote.model.ArticlesItem
import com.dicoding.asclepius.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel(){

    val history: LiveData<Result<List<HistoryEntity>>> = repository.getHistory()
    fun saveHistory(history: HistoryEntity) = repository.saveHistory(history)

    private val _newsArticles= MutableLiveData<Result<List<ArticlesItem>>>()
    val newsArticles: LiveData<Result<List<ArticlesItem>>>  = _newsArticles

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            _newsArticles.postValue(Result.Loading)
            try {
                val articles = repository.fetchNewsArticles()
                if (articles.isNotEmpty()) {
                    _newsArticles.postValue(Result.Success(articles))
                } else {
                    _newsArticles.postValue(Result.Error("No articles available"))
                }
            } catch (e: Exception) {
                _newsArticles.postValue(Result.Error(e.message ?: "Unknown Error"))
            }
        }
    }

    fun deleteHistory(id: Int) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }
}