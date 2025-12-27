package com.example.movieapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.Result
import com.example.movieapp.core.UiState
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
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

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 500L
        private const val MAX_DISPLAY_RESULTS = 18
    }

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

    private var searchJob: Job? = null

    init {
        loadAllMovies()
        setupSearch()
    }

    private fun setupSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collectLatest { query ->
                    try {
                        handleSearchQuery(query.trim())
                    } catch (e: Exception) {
                        _searchResults.value = UiState.Error("Search failed")
                    }
                }
        }
    }

    private suspend fun handleSearchQuery(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = UiState.Success(emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            try {
                performSearch(query)
            } catch (e: Exception) {
                _searchResults.value = UiState.Error("Search failed")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private suspend fun performSearch(query: String) {
        _searchResults.value = UiState.Loading

        val localResults = safeSearchLocal(query)
        if (localResults.isNotEmpty()) {
            _searchResults.value = UiState.Success(localResults.take(MAX_DISPLAY_RESULTS))
        }

        searchMoviesUseCase(query)
            .catch { e ->
                if (localResults.isNotEmpty()) {
                    emit(Result.Success(localResults))
                } else {
                    emit(Result.Error(Exception("Search failed")))
                }
            }
            .collect { result ->
                when (result) {
                    is Result.Loading -> {
                        if (localResults.isEmpty()) {
                            _searchResults.value = UiState.Loading
                        }
                    }
                    is Result.Success -> {
                        val apiResults = result.data.take(MAX_DISPLAY_RESULTS)
                        _searchResults.value = UiState.Success(apiResults)
                    }
                    is Result.Error -> {
                        if (localResults.isNotEmpty()) {
                            _searchResults.value = UiState.Success(localResults.take(MAX_DISPLAY_RESULTS))
                        } else {
                            _searchResults.value = UiState.Error("No results found")
                        }
                    }
                }
            }
    }

    private fun safeSearchLocal(query: String): List<Movie> {
        return try {
            val movies = mutableListOf<Movie>()

            (_nowPlaying.value as? UiState.Success)?.data?.let { movies.addAll(it) }
            (_upcoming.value as? UiState.Success)?.data?.let { movies.addAll(it) }
            (_topRated.value as? UiState.Success)?.data?.let { movies.addAll(it) }
            (_popular.value as? UiState.Success)?.data?.let { movies.addAll(it) }

            if (movies.isEmpty()) return emptyList()

            movies.asSequence()
                .filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.overview?.contains(query, ignoreCase = true) == true
                }
                .distinctBy { it.id }
                .toList()
        } catch (e: Exception) {
            emptyList()
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
            getNowPlayingMoviesUseCase()
                .catch { e -> emit(Result.Error(Exception(e))) }
                .collect { _nowPlaying.value = it.toUiState() }
        }
    }

    private fun loadUpcoming() {
        viewModelScope.launch {
            getUpcomingMoviesUseCase()
                .catch { e -> emit(Result.Error(Exception(e))) }
                .collect { _upcoming.value = it.toUiState() }
        }
    }

    private fun loadTopRated() {
        viewModelScope.launch {
            getTopRatedMoviesUseCase()
                .catch { e -> emit(Result.Error(Exception(e))) }
                .collect { _topRated.value = it.toUiState() }
        }
    }

    private fun loadPopular() {
        viewModelScope.launch {
            getPopularMoviesUseCase()
                .catch { e -> emit(Result.Error(Exception(e))) }
                .collect { _popular.value = it.toUiState() }
        }
    }

    fun retryLoadMovies() {
        loadAllMovies()
    }

    fun clearSearchResults() {
        try {
            searchJob?.cancel()
            _searchQuery.value = ""
            _searchResults.value = UiState.Success(emptyList())
        } catch (e: Exception) {
            // Handle silently
        }
    }

    private fun <T> Result<T>.toUiState(): UiState<T> = when (this) {
        is Result.Loading -> UiState.Loading
        is Result.Success -> UiState.Success(data)
        is Result.Error -> UiState.Error(exception.message ?: "Error occurred")
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}