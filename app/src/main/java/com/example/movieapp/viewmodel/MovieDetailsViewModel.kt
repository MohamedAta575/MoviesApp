package com.example.movieapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.CastMember
import com.example.movieapp.data.model.MovieDetails
import com.example.movieapp.data.model.Review
import com.example.movieapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel : ViewModel() {

    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails: StateFlow<MovieDetails?> = _movieDetails

    private val _cast = MutableStateFlow<List<CastMember>>(emptyList())
    val cast: StateFlow<List<CastMember>> = _cast
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    fun fetchMovieReviews(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiMovie.getMovieReviews(movieId)
                _reviews.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiMovie.getMovieDetails(movieId)
                _movieDetails.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchMovieCredits(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiMovie.getMovieCredits(movieId)
                _cast.value = response.cast
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
