package com.example.movieapp.data

import com.example.movieapp.data.local.BookmarkDao
import com.example.movieapp.data.local.BookmarkedMovieEntity
import kotlinx.coroutines.flow.Flow


class BookmarkRepository(private val bookmarkDao: BookmarkDao) {

    fun getAllBookmarks(): Flow<List<BookmarkedMovieEntity>> {
        return bookmarkDao.getAllBookmarkedMovies()
    }

    suspend fun addBookmark(movie: BookmarkedMovieEntity) {
        bookmarkDao.addBookmark(movie)
    }

    suspend fun removeBookmark(movie: BookmarkedMovieEntity) {
        bookmarkDao.removeBookmark(movie)
    }

    fun isBookmarked(movieId: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(movieId)
    }
}
