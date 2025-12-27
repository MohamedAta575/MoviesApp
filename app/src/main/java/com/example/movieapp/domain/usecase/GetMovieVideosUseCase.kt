package com.example.movieapp.domain.usecase

import com.example.movieapp.core.Result
import com.example.movieapp.domain.model.MovieVideo
import com.example.movieapp.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val repository: IMovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Result<List<MovieVideo>>> {
        return repository.getMovieVideos(movieId)
    }
}