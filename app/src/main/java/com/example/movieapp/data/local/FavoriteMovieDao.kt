package com.example.movieapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarked_movies")
    fun getAllBookmarkedMovies(): Flow<List<BookmarkedMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(movie: BookmarkedMovieEntity)

    @Delete
    suspend fun removeBookmark(movie: BookmarkedMovieEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_movies WHERE id = :movieId)")
    fun isBookmarked(movieId: Int): Flow<Boolean>
}