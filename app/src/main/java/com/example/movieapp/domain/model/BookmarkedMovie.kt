package com.example.movieapp.domain.model

data class BookmarkedMovie(
    val id: Int,
    val title: String,
    val posterPath: String,
    val voteAverage: Double,
    val releaseDate: String,
    val runtime: Int?
)
