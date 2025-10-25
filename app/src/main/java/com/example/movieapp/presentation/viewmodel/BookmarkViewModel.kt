package com.example.movieapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.BookmarkedMovie
import com.example.movieapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getAllBookmarksUseCase: GetAllBookmarksUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val removeBookmarkUseCase: RemoveBookmarkUseCase,
    private val isBookmarkedUseCase: IsBookmarkedUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    val bookmarkedMovies: StateFlow<List<BookmarkedMovie>> =
        getAllBookmarksUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun isBookmarked(movieId: Int): StateFlow<Boolean> {
        return isBookmarkedUseCase(movieId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    }

    fun addBookmark(movie: BookmarkedMovie) {
        viewModelScope.launch {
            addBookmarkUseCase(movie)
        }
    }

    fun removeBookmark(movie: BookmarkedMovie) {
        viewModelScope.launch {
            removeBookmarkUseCase(movie)
        }
    }

    fun toggleBookmark(movie: BookmarkedMovie, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            toggleBookmarkUseCase(movie, isCurrentlyBookmarked)
        }
    }
}