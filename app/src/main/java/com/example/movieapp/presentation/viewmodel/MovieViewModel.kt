package com.example.movieapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.Result
import com.example.movieapp.core.UiState
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _nowPlaying = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val nowPlaying: StateFlow<UiState<List<Movie>>> = _nowPlaying.asStateFlow()

    private val _upcoming = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val upcoming: StateFlow<UiState<List<Movie>>> = _upcoming.asStateFlow()

    private val _topRated = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val topRated: StateFlow<UiState<List<Movie>>> = _topRated.asStateFlow()

    private val _popular = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val popular: StateFlow<UiState<List<Movie>>> = _popular.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<UiState<List<Movie>>>(UiState.Success(emptyList()))
    val searchResults: StateFlow<UiState<List<Movie>>> = _searchResults.asStateFlow()

    init {
        loadAllMovies()
        setupSearch()
    }

    private fun setupSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _searchResults.value = UiState.Success(emptyList())
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private suspend fun performSearch(query: String) {
        // Local search first
        val allMovies = listOfNotNull(
            (_nowPlaying.value as? UiState.Success)?.data,
            (_upcoming.value as? UiState.Success)?.data,
            (_topRated.value as? UiState.Success)?.data,
            (_popular.value as? UiState.Success)?.data
        ).flatten()

        val localResults = allMovies.filter {
            it.title.contains(query, ignoreCase = true)
        }

        if (localResults.isNotEmpty()) {
            _searchResults.value = UiState.Success(localResults)
        } else {
            _searchResults.value = UiState.Loading
        }

        // API search
        searchMoviesUseCase(query).collect { result ->
            _searchResults.value = when (result) {
                is Result.Loading -> {
                    if (localResults.isEmpty()) UiState.Loading
                    else UiState.Success(localResults)
                }
                is Result.Success -> UiState.Success(result.data)
                is Result.Error -> {
                    if (localResults.isNotEmpty()) {
                        UiState.Success(localResults)
                    } else {
                        UiState.Error(result.exception.message ?: "Search failed")
                    }
                }
            }
        }
    }

    private fun loadAllMovies() {
        loadNowPlaying()
        loadUpcoming()
        loadTopRated()
        loadPopular()
    }

    private fun loadNowPlaying() {
        viewModelScope.launch {
            getNowPlayingMoviesUseCase().collect { result ->
                _nowPlaying.value = result.toUiState()
            }
        }
    }

    private fun loadUpcoming() {
        viewModelScope.launch {
            getUpcomingMoviesUseCase().collect { result ->
                _upcoming.value = result.toUiState()
            }
        }
    }

    private fun loadTopRated() {
        viewModelScope.launch {
            getTopRatedMoviesUseCase().collect { result ->
                _topRated.value = result.toUiState()
            }
        }
    }

    private fun loadPopular() {
        viewModelScope.launch {
            getPopularMoviesUseCase().collect { result ->
                _popular.value = result.toUiState()
            }
        }
    }

    fun retryLoadMovies() {
        loadAllMovies()
    }

    fun clearSearchResults() {
        _searchQuery.value = ""
        _searchResults.value = UiState.Success(emptyList())
    }

    private fun <T> Result<T>.toUiState(): UiState<T> = when (this) {
        is Result.Loading -> UiState.Loading
        is Result.Success -> UiState.Success(data)
        is Result.Error -> UiState.Error(exception.message ?: "Error occurred")
    }
}