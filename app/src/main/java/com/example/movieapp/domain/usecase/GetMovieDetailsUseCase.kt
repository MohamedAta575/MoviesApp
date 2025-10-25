package com.example.movieapp.domain.usecase

import com.example.movieapp.core.Result
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: IMovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Result<Movie>> {
        return repository.getMovieDetails(movieId)
    }
}
