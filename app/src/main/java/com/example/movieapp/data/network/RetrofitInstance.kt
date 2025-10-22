package com.example.movieapp.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyNTk0NjgxNTU2ZDg4MjUwYjEyYmQ0YzNkMzBlMTk1MiIsIm5iZiI6MTc1MjM2MzQzNy44OCwic3ViIjoiNjg3MmYxYWQ0N2Q1OWE4OTZhYTk3ZGZhIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.WDjOEGeskv35bRCcjBV51rUAAV4UhWqbe5nZVYj84F8") // استخدم الـ token كامل هنا
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiMovie: MovieApiService = retrofit.create(MovieApiService::class.java)
}