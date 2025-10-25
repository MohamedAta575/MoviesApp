package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.BookmarkedMovie
import com.example.movieapp.domain.repository.IBookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveBookmarkUseCase @Inject constructor(
    private val repository: IBookmarkRepository
) {
    suspend operator fun invoke(movie: BookmarkedMovie) {
        repository.removeBookmark(movie)
    }
}