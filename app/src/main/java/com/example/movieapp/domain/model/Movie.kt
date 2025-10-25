package com.example.movieapp.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val overview: String,
    val voteAverage: Float,
    val releaseDate: String,
    val genres: List<String>,
    val runtime: Int?
)
