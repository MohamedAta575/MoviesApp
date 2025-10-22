package com.example.movieapp

import com.example.movieapp.data.local.BookmarkDao
import com.example.movieapp.data.local.BookmarkedMovieEntity
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val dao: BookmarkDao) {

    fun getAllBookmarkedMovies(): Flow<List<BookmarkedMovieEntity>> = dao.getAllBookmarkedMovies()

    suspend fun addBookmark(movie: BookmarkedMovieEntity) = dao.addBookmark(movie)

    suspend fun removeBookmark(movie: BookmarkedMovieEntity) = dao.removeBookmark(movie)

    fun isBookmarked(movieId: Int): Flow<Boolean> = dao.isBookmarked(movieId)
}