package com.example.movieapp.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val results: List<MovieDetails>
)

data class Genre(
    val id: Int,
    val name: String
)

data class CreditsResponse(
    val cast: List<CastMember>
)

data class CastMember(
    val id: Int,
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?
)

data class Review(
    val id: String,
    val author: String,
    val content: String
)

data class ReviewsResponse(
    val results: List<Review>
)

data class MovieDetails(
    val id: Int,
    val title: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    val overview: String,

    @SerializedName("vote_average")
    val voteAverage: Float,

    @SerializedName("release_date")
    val releaseDate: String,

    val genres: List<Genre>?,

    val runtime: Int? = null
)

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    @SerializedName("official")
    val official: Boolean = false
)

data class VideosResponse(
    val results: List<Video>
)