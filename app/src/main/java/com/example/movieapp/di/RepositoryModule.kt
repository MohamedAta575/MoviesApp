package com.example.movieapp.di

import com.example.movieapp.data.local.BookmarkDao
import com.example.movieapp.data.network.MovieApiService
import com.example.movieapp.data.repository.BookmarkRepositoryImpl
import com.example.movieapp.data.repository.MovieRepositoryImpl
import com.example.movieapp.domain.repository.IBookmarkRepository
import com.example.movieapp.domain.repository.IMovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(
        apiService: MovieApiService
    ): IMovieRepository {
        return MovieRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(
        bookmarkDao: BookmarkDao
    ): IBookmarkRepository {
        return BookmarkRepositoryImpl(bookmarkDao)
    }
}