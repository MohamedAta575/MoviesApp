package com.example.movieapp.domain.repository

import com.example.movieapp.domain.model.BookmarkedMovie
import kotlinx.coroutines.flow.Flow

interface IBookmarkRepository {
    fun getAllBookmarks(): Flow<List<BookmarkedMovie>>
    suspend fun addBookmark(movie: BookmarkedMovie)
    suspend fun removeBookmark(movie: BookmarkedMovie)
    fun isBookmarked(movieId: Int): Flow<Boolean>
    suspend fun toggleBookmark(movie: BookmarkedMovie, isCurrentlyBookmarked: Boolean)
}