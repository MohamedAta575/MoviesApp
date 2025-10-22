package com.example.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.MovieDetails
import com.example.movieapp.data.model.Review
import com.example.movieapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _movies = MutableStateFlow<List<MovieDetails>>(emptyList())
    val movies: StateFlow<List<MovieDetails>> = _movies

    private val _nowPlaying = MutableStateFlow<List<MovieDetails>>(emptyList())
    val nowPlaying: StateFlow<List<MovieDetails>> = _nowPlaying

    private val _upcoming = MutableStateFlow<List<MovieDetails>>(emptyList())
    val upcoming: StateFlow<List<MovieDetails>> = _upcoming

    private val _topRated = MutableStateFlow<List<MovieDetails>>(emptyList())
    val topRated: StateFlow<List<MovieDetails>> = _topRated

    private val _popular = MutableStateFlow<List<MovieDetails>>(emptyList())
    val popular: StateFlow<List<MovieDetails>> = _popular

    private val _detailedSearchResults = MutableStateFlow<List<MovieDetails>>(emptyList())
    val detailedSearchResults: StateFlow<List<MovieDetails>> = _detailedSearchResults




    init {
        getMovies()
    }


    private fun getMovies() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiMovie.getPopularMovies()
                _movies.value = response.results
                _nowPlaying.value = RetrofitInstance.apiMovie.getNowPlayingMovies().results
                _upcoming.value = RetrofitInstance.apiMovie.getUpcomingMovies().results
                _topRated.value = RetrofitInstance.apiMovie.getTopRatedMovies().results
                _popular.value = RetrofitInstance.apiMovie.getPopularMovies().results

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiMovie.searchMovies(query)
                val movies = response.results

                val detailedResults = movies.mapNotNull { movie ->
                    try {
                        RetrofitInstance.apiMovie.getMovieDetails(movie.id)
                    } catch (e: Exception) {
                        null
                    }
                }

                _detailedSearchResults.value = detailedResults
            } catch (e: Exception) {
                e.printStackTrace()
                _detailedSearchResults.value = emptyList()
            }
        }
    }

}