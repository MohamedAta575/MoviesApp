package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.BookmarkedMovie
import com.example.movieapp.domain.repository.IBookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBookmarksUseCase @Inject constructor(
    private val repository: IBookmarkRepository
) {
    operator fun invoke(): Flow<List<BookmarkedMovie>> {
        return repository.getAllBookmarks()
    }
}