package com.example.movieapp.domain.model

data class MovieVideo(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val isOfficial: Boolean
)