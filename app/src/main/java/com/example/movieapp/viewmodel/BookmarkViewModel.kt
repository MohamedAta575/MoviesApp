package com.example.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.BookmarkRepository
import com.example.movieapp.data.local.BookmarkedMovieEntity
import com.example.movieapp.data.model.MovieDetails
import com.example.movieapp.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: BookmarkRepository
) : ViewModel() {

    val bookmarkedMovies = repository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBookmark(movie: BookmarkedMovieEntity) {
        viewModelScope.launch {
            repository.addBookmark(movie)
        }
    }

    fun removeBookmark(movie: BookmarkedMovieEntity) {
        viewModelScope.launch {
            repository.removeBookmark(movie)
        }
    }

    fun isBookmarked(movieId: Int) = repository.isBookmarked(movieId)

    fun toggleBookmark(movie: BookmarkedMovieEntity, isBookmarked: Boolean) {
        viewModelScope.launch {
            if (isBookmarked) {
                repository.removeBookmark(movie)
            } else {
                repository.addBookmark(movie)
            }
        }
    }
}
