package com.example.movieapp.ui.screens

import com.example.movieapp.data.local.BookmarkedMovieEntity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.R
import com.example.movieapp.data.model.CastMember
import com.example.movieapp.data.model.Review
import com.example.movieapp.toEntity
import com.example.movieapp.ui.theme.Background_color
import com.example.movieapp.ui.theme.poppinsFontFamily
import com.example.movieapp.ui.theme.star_icon
import com.example.movieapp.ui.viewmodel.MovieDetailsViewModel
import com.example.movieapp.viewmodel.BookmarkViewModel

@Composable
fun DetailsScreen(
    movieId: Int,
    navController: NavController,
    viewModel: MovieDetailsViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    val movie by viewModel.movieDetails.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val cast by viewModel.cast.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val isBookmarked by bookmarkViewModel.isBookmarked(movieId).collectAsState(initial = false)

    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetails(movieId)
        viewModel.fetchMovieCredits(movieId)
        viewModel.fetchMovieReviews(movieId)
    }

    movie?.let { movieDetails ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background_color)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background_color)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    text = "Details",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    painter = if (isBookmarked)
                        painterResource(id = R.drawable.book_mark_is_enable)
                    else
                        painterResource(id = R.drawable.path_33968),
                    contentDescription = "Bookmark",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            bookmarkViewModel.toggleBookmark(movieDetails.toEntity(),isBookmarked)
                        }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movieDetails.backdropPath}"),
                    contentDescription = "Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startY = size.height / 3,
                                    endY = size.height
                                )
                            )
                        }
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w185${movieDetails.posterPath}"),
                        contentDescription = "Poster",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = movieDetails.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = "Rating",
                                tint = star_icon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${movieDetails.voteAverage}/10",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = movieDetails.releaseDate.take(4),
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                            Text(
                                text = "${movieDetails.runtime} min",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                            movieDetails.genres?.let {
                                Text(
                                    text = it.joinToString(", ") { it.name },
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val tabs = listOf("About Movie", "Reviews", "Cast")
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Background_color
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) Color.White else Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = poppinsFontFamily
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> AboutMovieSection(movieDetails.overview)
                1 -> ReviewsSection(reviews)
                2 -> CastSection(cast)
            }
        }
    }
}
//Tab: About the movie
@Composable
fun AboutMovieSection(overview: String) {
    Text(
        text = overview,
        color = Color.White,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

// Tab: Reviews
@Composable
fun ReviewsSection(reviews: List<Review>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        if (reviews.isEmpty()) {
            Text(
                text = "No reviews available.",
                color = Color.LightGray
            )
        } else {
            reviews.forEach { review ->
                Text(
                    text = "${review.author}:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "\"${review.content.take(200)}...\"",
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// Tab: Cast
@Composable
fun CastSection(cast: List<CastMember>) {
    LazyRow(
        modifier = Modifier.padding(start = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cast) { actor ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (actor.profilePath != null) {
                    Image(
                        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w185${actor.profilePath}"),
                        contentDescription = actor.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.Gray, shape = MaterialTheme.shapes.medium)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = actor.name, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
