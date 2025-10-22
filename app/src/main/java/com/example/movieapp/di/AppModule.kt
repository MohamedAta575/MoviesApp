package com.example.movieapp.di

import android.content.Context
import androidx.room.Room
import com.example.movieapp.data.BookmarkRepository
import com.example.movieapp.data.local.BookmarkDao
import com.example.movieapp.data.local.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MovieDatabase {
        return Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movie_app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(
        database: MovieDatabase
    ): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(
        dao: BookmarkDao
    ): BookmarkRepository {
        return BookmarkRepository(dao)
    }
}
