package com.dicoding.asclepius.data.remote.retrofit

import android.util.Log
import com.dicoding.asclepius.BuildConfig.API_KEY
import com.dicoding.asclepius.BuildConfig.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val IS_DEBUG = true
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (IS_DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

   private val apiKeyInterceptor = Interceptor{ chain ->
        val oriRequest = chain.request()
        val requestWithKey = oriRequest.newBuilder()
        if(API_KEY.isNotBlank()){
            requestWithKey.addHeader("Authorization", "Bearer $API_KEY")
        }else{
            Log.e("ApiConfig", "API key hilang!!")
        }
        chain.proceed(requestWithKey.build())
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val apiService : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}