package com.dicoding.asclepius.view.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.dicoding.asclepius.data.repository.Repository
import com.dicoding.asclepius.di.Injection

class ViewModelFactory (private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(MainViewModel::class.java) ->{
                MainViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown MainViewModel class : ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideRepo(context)
                )
            }.also { INSTANCE = it }
    }
}