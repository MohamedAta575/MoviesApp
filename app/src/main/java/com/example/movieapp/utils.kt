package com.example.movieapp

import com.example.movieapp.data.local.BookmarkedMovieEntity
import com.example.movieapp.data.model.MovieDetails

fun MovieDetails.toEntity(): BookmarkedMovieEntity {
    return BookmarkedMovieEntity(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath ?: "",
        voteAverage = this.voteAverage.toDouble(),
        releaseDate = this.releaseDate,
        runtime = this.runtime
    )
}

