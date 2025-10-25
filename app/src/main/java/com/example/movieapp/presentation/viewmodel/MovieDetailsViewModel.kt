package com.example.movieapp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.Result
import com.example.movieapp.core.UiState
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieReview
import com.example.movieapp.domain.usecase.GetMovieCreditsUseCase
import com.example.movieapp.domain.usecase.GetMovieDetailsUseCase
import com.example.movieapp.domain.usecase.GetMovieReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle.get<Int>("movieId"))

    private val _movieDetails = MutableStateFlow<UiState<Movie>>(UiState.Loading)
    val movieDetails: StateFlow<UiState<Movie>> = _movieDetails.asStateFlow()

    private val _cast = MutableStateFlow<UiState<List<Cast>>>(UiState.Loading)
    val cast: StateFlow<UiState<List<Cast>>> = _cast.asStateFlow()

    private val _reviews = MutableStateFlow<UiState<List<MovieReview>>>(UiState.Loading)
    val reviews: StateFlow<UiState<List<MovieReview>>> = _reviews.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        fetchMovieDetails()
        fetchMovieCredits()
        fetchMovieReviews()
    }

    private fun fetchMovieDetails() {
        viewModelScope.launch {
            getMovieDetailsUseCase(movieId).collect { result ->
                _movieDetails.value = result.toUiState()
            }
        }
    }

    private fun fetchMovieCredits() {
        viewModelScope.launch {
            getMovieCreditsUseCase(movieId).collect { result ->
                _cast.value = result.toUiState()
            }
        }
    }

    private fun fetchMovieReviews() {
        viewModelScope.launch {
            getMovieReviewsUseCase(movieId).collect { result ->
                _reviews.value = result.toUiState()
            }
        }
    }

    fun retry() {
        loadMovieDetails()
    }

    private fun <T> Result<T>.toUiState(): UiState<T> = when (this) {
        is Result.Loading -> UiState.Loading
        is Result.Success -> UiState.Success(data)
        is Result.Error -> UiState.Error(exception.message ?: "Error occurred")
    }
}