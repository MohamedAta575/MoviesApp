package com.example.movieapp.data.repository

import com.example.movieapp.core.Result
import com.example.movieapp.data.mapper.toDomain
import com.example.movieapp.data.network.MovieApiService
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieReview
import com.example.movieapp.domain.repository.IMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService
) : IMovieRepository {

    override fun getPopularMovies(): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPopularMovies()
            emit(Result.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getNowPlayingMovies(): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getNowPlayingMovies()
            emit(Result.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getUpcomingMovies(): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getUpcomingMovies()
            emit(Result.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getTopRatedMovies(): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getTopRatedMovies()
            emit(Result.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun searchMovies(query: String): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.searchMovies(query)
            emit(Result.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMovieDetails(movieId: Int): Flow<Result<Movie>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMovieDetails(movieId)
            emit(Result.Success(response.toDomain()))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMovieCredits(movieId: Int): Flow<Result<List<Cast>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMovieCredits(movieId)
            emit(Result.Success(response.cast.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMovieReviews(movieId: Int): Flow<Result<List<MovieReview>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMovieReviews(movieId)
            emit(Result.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}