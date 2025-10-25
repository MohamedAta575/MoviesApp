package com.example.movieapp.data.repository

import com.example.movieapp.data.local.BookmarkDao
import com.example.movieapp.data.mapper.toDomain
import com.example.movieapp.data.mapper.toEntity
import com.example.movieapp.domain.model.BookmarkedMovie
import com.example.movieapp.domain.repository.IBookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : IBookmarkRepository {

    override fun getAllBookmarks(): Flow<List<BookmarkedMovie>> {
        return bookmarkDao.getAllBookmarkedMovies()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun addBookmark(movie: BookmarkedMovie) {
        withContext(Dispatchers.IO) {
            bookmarkDao.addBookmark(movie.toEntity())
        }
    }

    override suspend fun removeBookmark(movie: BookmarkedMovie) {
        withContext(Dispatchers.IO) {
            bookmarkDao.removeBookmark(movie.toEntity())
        }
    }

    override fun isBookmarked(movieId: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(movieId)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun toggleBookmark(movie: BookmarkedMovie, isCurrentlyBookmarked: Boolean) {
        withContext(Dispatchers.IO) {
            if (isCurrentlyBookmarked) {
                bookmarkDao.removeBookmark(movie.toEntity())
            } else {
                bookmarkDao.addBookmark(movie.toEntity())
            }
        }
    }
}