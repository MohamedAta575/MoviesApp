package com.example.movieapp.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movieapp.R
import com.example.movieapp.core.UiState
import com.example.movieapp.presentation.mapper.*
import com.example.movieapp.presentation.screens.components.ErrorStateView
import com.example.movieapp.presentation.theme.*
import com.example.movieapp.presentation.viewmodel.BookmarkViewModel
import com.example.movieapp.presentation.viewmodel.MovieDetailsViewModel

@Composable
fun DetailsScreen(
    movieId: Int,
    navController: NavController,
    detailsViewModel: MovieDetailsViewModel = hiltViewModel(),
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    val movieDetailsState by detailsViewModel.movieDetails.collectAsState()
    val reviewsState by detailsViewModel.reviews.collectAsState()
    val castState by detailsViewModel.cast.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val isBookmarked by bookmarkViewModel.isBookmarked(movieId).collectAsState()

    AnimatedContent(
        targetState = movieDetailsState,
        transitionSpec = {
            fadeIn(animationSpec = tween(400)) togetherWith
                    fadeOut(animationSpec = tween(400))
        },
        label = "details_content"
    ) { state ->
        when (state) {
            is UiState.Loading -> {
                DetailsLoadingShimmer()
            }
            is UiState.Success -> {
                val movie = state.data.toUi()
                MovieDetailsContent(
                    movie = movie,
                    reviewsState = reviewsState,
                    castState = castState,
                    isBookmarked = isBookmarked,
                    selectedTabIndex = selectedTabIndex,
                    onTabIndexChanged = { selectedTabIndex = it },
                    onBackClick = { navController.popBackStack() },
                    onBookmarkClick = {
                        val bookmarkedMovie = movie.toDomain().let {
                            com.example.movieapp.domain.model.BookmarkedMovie(
                                id = it.id,
                                title = it.title,
                                posterPath = it.posterPath,
                                voteAverage = it.voteAverage.toDouble(),
                                releaseDate = it.releaseDate,
                                runtime = it.runtime
                            )
                        }
                        bookmarkViewModel.toggleBookmark(bookmarkedMovie, isBookmarked)
                    }
                )
            }
            is UiState.Error -> {
                ErrorStateView(
                    message = state.message,
                    onRetry = { detailsViewModel.retry() }
                )
            }
        }
    }
}

@Composable
fun MovieDetailsContent(
    movie: MovieUi,
    reviewsState: UiState<List<com.example.movieapp.domain.model.MovieReview>>,
    castState: UiState<List<com.example.movieapp.domain.model.Cast>>,
    isBookmarked: Boolean,
    selectedTabIndex: Int,
    onTabIndexChanged: (Int) -> Unit,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background_color)
                .verticalScroll(scrollState)
        ) {
            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://image.tmdb.org/t/p/original${movie.backdropPath}")
                        .crossfade(500)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Background_color.copy(alpha = 0.7f),
                                        Background_color
                                    ),
                                    startY = 0f,
                                    endY = size.height
                                )
                            )
                        }
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Card(
                            modifier = Modifier.size(width = 120.dp, height = 180.dp),
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(12.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                                    .crossfade(400)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_background)
                                    .build(),
                                contentDescription = movie.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = movie.title,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = poppinsFontFamily,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = star_icon.copy(alpha = 0.2f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.star),
                                        contentDescription = "Rating",
                                        tint = star_icon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = movie.formattedRating,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "/10",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                MetaChip(text = movie.year)
                                MetaChip(text = movie.runtimeFormatted)
                            }
                        }
                    }
                }
            }

            // Genres
            if (movie.genres.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movie.genres) { genre ->
                        GenreChip(genre)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedTabs(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = onTabIndexChanged
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTabIndex) {
                0 -> AboutMovieSection(movie.overview)
                1 -> ReviewsSection(reviewsState)
                2 -> CastSection(castState)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        FloatingActionBar(
            isBookmarked = isBookmarked,
            onBackClick = onBackClick,
            onBookmarkClick = onBookmarkClick
        )
    }
}

@Composable
fun FloatingActionBar(
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FloatingActionButton(
            onClick = onBackClick,
            containerColor = Color.Black.copy(alpha = 0.7f),
            contentColor = Color.White,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier.size(28.dp)
            )
        }

        FloatingActionButton(
            onClick = onBookmarkClick,
            containerColor = if (isBookmarked) Icon_color else Color.Black.copy(alpha = 0.7f),
            contentColor = Color.White,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isBookmarked) R.drawable.book_mark_is_enable
                    else R.drawable.path_33968
                ),
                contentDescription = "Bookmark",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AnimatedTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("About", "Reviews", "Cast")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Background_color,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Icon_color,
                height = 3.dp
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) Color.White else Color.Gray,
                        fontSize = 15.sp,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = poppinsFontFamily
                    )
                }
            )
        }
    }
}

@Composable
fun MetaChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun GenreChip(genre: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Icon_color.copy(alpha = 0.2f),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Text(
            text = genre,
            fontSize = 13.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun AboutMovieSection(overview: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2D35)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = overview,
            color = Color.White,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ReviewsSection(reviewsState: UiState<List<com.example.movieapp.domain.model.MovieReview>>) {
    when (reviewsState) {
        is UiState.Loading -> {
            LoadingIndicator()
        }
        is UiState.Success -> {
            val reviews = reviewsState.data.map { it.toUi() }
            if (reviews.isEmpty()) {
                EmptyStateMessage("No reviews yet", "📝")
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    reviews.take(5).forEach { review ->
                        ReviewCard(review)
                    }
                }
            }
        }
        is UiState.Error -> {
            EmptyStateMessage(reviewsState.message, "⚠️")
        }
    }
}

@Composable
fun ReviewCard(review: ReviewUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2D35)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Icon_color,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = review.authorInitial,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                Text(
                    text = review.author,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = review.contentPreview,
                color = Color.LightGray,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun CastSection(castState: UiState<List<com.example.movieapp.domain.model.Cast>>) {
    when (castState) {
        is UiState.Loading -> {
            LoadingIndicator()
        }
        is UiState.Success -> {
            val cast = castState.data.map { it.toUi() }
            if (cast.isEmpty()) {
                EmptyStateMessage("No cast information", "🎭")
            } else {
                LazyRow(
                    modifier = Modifier.padding(start = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 20.dp)
                ) {
                    items(cast.take(15), key = { it.id }) { actor ->
                        CastMemberCard(actor)
                    }
                }
            }
        }
        is UiState.Error -> {
            EmptyStateMessage(castState.message, "⚠️")
        }
    }
}

@Composable
fun CastMemberCard(actor: CastUi) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Card(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            if (actor.profilePath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://image.tmdb.org/t/p/w185${actor.profilePath}")
                        .crossfade(300)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .build(),
                    contentDescription = actor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Icon_color, Icon_color.copy(alpha = 0.6f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = actor.initial,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = actor.name,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Icon_color, strokeWidth = 3.dp)
    }
}

@Composable
fun EmptyStateMessage(message: String, icon: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun DetailsLoadingShimmer() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background_color)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3A3F47),
                                Background_color
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF3A3F47))
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .height(20.dp)
                                        .fillMaxWidth(0.7f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF3A3F47))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(20.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF3A3F47))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp),
            color = Icon_color,
            strokeWidth = 3.dp
        )
    }
}

