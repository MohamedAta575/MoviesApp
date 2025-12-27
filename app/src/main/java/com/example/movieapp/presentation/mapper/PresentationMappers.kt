package com.example.movieapp.presentation.mapper

import com.example.movieapp.domain.model.BookmarkedMovie
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.MovieReview
import com.example.movieapp.domain.model.MovieVideo

// ========== Domain to Presentation ==========

// Movie UI Model
data class MovieUi(
    val id: Int,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val overview: String,
    val voteAverage: Float,
    val releaseDate: String,
    val genres: List<String>,
    val runtime: Int?,
    val formattedRating: String,
    val year: String,
    val runtimeFormatted: String
)

fun Movie.toUi(): MovieUi {
    return MovieUi(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        overview = this.overview,
        voteAverage = this.voteAverage,
        releaseDate = this.releaseDate,
        genres = this.genres,
        runtime = this.runtime,
        formattedRating = String.format("%.1f", this.voteAverage),
        year = this.releaseDate.take(4),
        runtimeFormatted = this.runtime?.let { "$it min" } ?: "N/A"
    )
}

// Cast UI Model
data class CastUi(
    val id: Int,
    val name: String,
    val profilePath: String?,
    val initial: String
)

fun Cast.toUi(): CastUi {
    return CastUi(
        id = this.id,
        name = this.name,
        profilePath = this.profilePath,
        initial = this.name.take(1).uppercase()
    )
}

// Review UI Model
data class ReviewUi(
    val id: String,
    val author: String,
    val content: String,
    val authorInitial: String,
    val contentPreview: String
)

fun MovieReview.toUi(): ReviewUi {
    return ReviewUi(
        id = this.id,
        author = this.author,
        content = this.content,
        authorInitial = this.author.take(1).uppercase(),
        contentPreview = if (this.content.length > 200) {
            this.content.take(200) + "..."
        } else {
            this.content
        }
    )
}

// BookmarkedMovie UI Model
data class BookmarkedMovieUi(
    val id: Int,
    val title: String,
    val posterPath: String,
    val voteAverage: Double,
    val releaseDate: String,
    val runtime: Int?,
    val formattedRating: String,
    val year: String,
    val runtimeFormatted: String
)

fun BookmarkedMovie.toUi(): BookmarkedMovieUi {
    return BookmarkedMovieUi(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        voteAverage = this.voteAverage,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        formattedRating = String.format("%.1f", this.voteAverage),
        year = this.releaseDate.take(4),
        runtimeFormatted = this.runtime?.let { "$it min" } ?: "N/A"
    )
}

// UI to Domain
fun MovieUi.toDomain(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        overview = this.overview,
        voteAverage = this.voteAverage,
        releaseDate = this.releaseDate,
        genres = this.genres,
        runtime = this.runtime
    )
}

fun BookmarkedMovieUi.toDomain(): BookmarkedMovie {
    return BookmarkedMovie(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        voteAverage = this.voteAverage,
        releaseDate = this.releaseDate,
        runtime = this.runtime
    )
}
data class VideoUi(
    val id: String,
    val key: String,
    val name: String,
    val thumbnailUrl: String,
    val youtubeUrl: String
)

fun MovieVideo.toUi(): VideoUi {
    return VideoUi(
        id = id,
        key = key,
        name = name,
        thumbnailUrl = "https://img.youtube.com/vi/$key/hqdefault.jpg",
        youtubeUrl = "https://www.youtube.com/watch?v=$key"
    )
}