package com.example.movieapp.domain.repository

import com.example.movieapp.core.Result
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieReview
import com.example.movieapp.domain.model.MovieVideo
import kotlinx.coroutines.flow.Flow

interface IMovieRepository {
    fun getPopularMovies(): Flow<Result<List<Movie>>>
    fun getNowPlayingMovies(): Flow<Result<List<Movie>>>
    fun getUpcomingMovies(): Flow<Result<List<Movie>>>
    fun getTopRatedMovies(): Flow<Result<List<Movie>>>
    fun searchMovies(query: String): Flow<Result<List<Movie>>>
    fun getMovieDetails(movieId: Int): Flow<Result<Movie>>
    fun getMovieCredits(movieId: Int): Flow<Result<List<Cast>>>
    fun getMovieReviews(movieId: Int): Flow<Result<List<MovieReview>>>
    fun getMovieVideos(movieId: Int): Flow<Result<List<MovieVideo>>>

}