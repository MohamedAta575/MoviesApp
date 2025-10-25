package com.example.movieapp.data.mapper

import com.example.movieapp.data.local.BookmarkedMovieEntity
import com.example.movieapp.data.model.CastMember
import com.example.movieapp.data.model.MovieDetails
import com.example.movieapp.data.model.Review
import com.example.movieapp.domain.model.BookmarkedMovie
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieReview

// ========== Data to Domain ==========

fun MovieDetails.toDomain(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath ?: "",
        backdropPath = this.backdropPath ?: "",
        overview = this.overview,
        voteAverage = this.voteAverage,
        releaseDate = this.releaseDate,
        genres = this.genres?.map { it.name } ?: emptyList(),
        runtime = this.runtime
    )
}

fun CastMember.toDomain(): Cast {
    return Cast(
        id = this.id,
        name = this.name,
        profilePath = this.profilePath
    )
}

fun Review.toDomain(): MovieReview {
    return MovieReview(
        id = this.id,
        author = this.author,
        content = this.content
    )
}

fun BookmarkedMovieEntity.toDomain(): BookmarkedMovie {
    return BookmarkedMovie(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        voteAverage = this.voteAverage,
        releaseDate = this.releaseDate,
        runtime = this.runtime
    )
}

// ========== Domain to Data ==========

fun Movie.toEntity(): BookmarkedMovieEntity {
    return BookmarkedMovieEntity(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        voteAverage = this.voteAverage.toDouble(),
        releaseDate = this.releaseDate,
        runtime = this.runtime
    )
}

fun BookmarkedMovie.toEntity(): BookmarkedMovieEntity {
    return BookmarkedMovieEntity(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        voteAverage = this.voteAverage,
        releaseDate = this.releaseDate,
        runtime = this.runtime
    )
}