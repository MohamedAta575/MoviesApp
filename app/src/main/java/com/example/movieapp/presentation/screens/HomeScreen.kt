package com.example.movieapp.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movieapp.R
import com.example.movieapp.core.UiState
import com.example.movieapp.presentation.mapper.toUi
import com.example.movieapp.presentation.screens.components.ErrorStateView
import com.example.movieapp.presentation.theme.*
import com.example.movieapp.presentation.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MovieViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    var selectedTab by remember { mutableStateOf("Now Playing") }

    val nowPlayingState by viewModel.nowPlaying.collectAsState()
    val upcomingState by viewModel.upcoming.collectAsState()
    val topRatedState by viewModel.topRated.collectAsState()
    val popularState by viewModel.popular.collectAsState()

    val isSearching = searchQuery.isNotBlank()

    Scaffold(
        containerColor = Background_color,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            Column(
                modifier = Modifier
                    .background(Background_color)
                    .padding(top = 16.dp)
            ) {
                // Hide title when searching for cleaner look
                AnimatedVisibility(
                    visible = !isSearching,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = "What do you want to watch?",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Normal,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                    )
                }

                ImprovedSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onClearClick = { viewModel.clearSearchResults() },
                    isSearching = isSearching,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Animate between search results and normal content
            Crossfade(
                targetState = isSearching,
                animationSpec = tween(300),
                label = "content_crossfade"
            ) { searching ->
                if (searching) {
                    SearchResultsContent(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        navController = navController,
                        onRetry = { viewModel.updateSearchQuery(searchQuery) }
                    )
                } else {
                    NormalHomeContent(
                        popularState = popularState,
                        nowPlayingState = nowPlayingState,
                        upcomingState = upcomingState,
                        topRatedState = topRatedState,
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        navController = navController,
                        onRetry = { viewModel.retryLoadMovies() }
                    )
                }
            }
        }
    }
}

@Composable
fun ImprovedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search movies...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isSearching) Icon_color else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF3A3F47),
            unfocusedContainerColor = Color(0xFF3A3F47),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Icon_color,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

@Composable
fun SearchResultsContent(
    searchQuery: String,
    searchResults: UiState<List<com.example.movieapp.domain.model.Movie>>,
    navController: NavController,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search header with result count
        AnimatedContent(
            targetState = searchResults,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                        fadeOut(animationSpec = tween(200))
            },
            label = "search_header"
        ) { state ->
            if (state is UiState.Success && state.data.isNotEmpty()) {
                Text(
                    text = "Found ${state.data.size} results for \"$searchQuery\"",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Search results grid
        AnimatedContent(
            targetState = searchResults,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "search_results",
            modifier = Modifier.fillMaxSize()
        ) { state ->
            when (state) {
                is UiState.Loading -> {
                    SearchLoadingState()
                }
                is UiState.Success -> {
                    val movies = state.data.map { it.toUi() }
                    if (movies.isEmpty()) {
                        EmptySearchResults(searchQuery)
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(movies, key = { it.id }) { movie ->
                                MovieGridItemEnhanced(
                                    movie = movie,
                                    onClick = { navController.navigate("details/${movie.id}") }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    SearchErrorState(
                        message = state.message,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySearchResults(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🔍", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No results found",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We couldn't find any movies matching\n\"$query\"",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Try different keywords or check spelling",
            fontSize = 12.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SearchLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Icon_color,
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Searching...",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SearchErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "⚠️", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Search failed",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Icon_color),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Try Again")
        }
    }
}

@Composable
fun NormalHomeContent(
    popularState: UiState<List<com.example.movieapp.domain.model.Movie>>,
    nowPlayingState: UiState<List<com.example.movieapp.domain.model.Movie>>,
    upcomingState: UiState<List<com.example.movieapp.domain.model.Movie>>,
    topRatedState: UiState<List<com.example.movieapp.domain.model.Movie>>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    navController: NavController,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Popular Movies Horizontal Scroll
        AnimatedContent(
            targetState = popularState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "popular_animation"
        ) { state ->
            when (state) {
                is UiState.Loading -> {
                    LoadingSection()
                }
                is UiState.Success -> {
                    val movies = state.data.map { it.toUi() }
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(movies, key = { it.id }) { movie ->
                            EnhancedMovieCard(
                                movie = movie,
                                onClick = { navController.navigate("details/${movie.id}") }
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    CompactErrorView(
                        message = state.message,
                        onRetry = onRetry
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Category Tabs
        AnimatedCategoryTabs(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )

        Spacer(Modifier.height(16.dp))

        // Selected Category Grid
        val currentState = when (selectedTab) {
            "Now Playing" -> nowPlayingState
            "Upcoming" -> upcomingState
            "Top Rated" -> topRatedState
            "Popular" -> popularState
            else -> nowPlayingState
        }

        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "grid_animation"
        ) { state ->
            when (state) {
                is UiState.Loading -> {
                    LoadingGrid()
                }
                is UiState.Success -> {
                    val movies = state.data.map { it.toUi() }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(movies, key = { it.id }) { movie ->
                            MovieGridItemEnhanced(
                                movie = movie,
                                onClick = { navController.navigate("details/${movie.id}") }
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorStateView(
                        message = state.message,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedCategoryTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val tabs = listOf("Now Playing", "Upcoming", "Top Rated", "Popular")
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "tab_scale"
            )

            Column(
                modifier = Modifier
                    .scale(scale)
                    .clickable { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontFamily = poppinsFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.animation.AnimatedVisibility(
                    visible = isSelected,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(24.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Icon_color, Icon_color.copy(alpha = 0.6f))
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedMovieCard(
    movie: com.example.movieapp.presentation.mapper.MovieUi,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box {
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 300f
                        )
                    )
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⭐", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = movie.formattedRating,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MovieGridItemEnhanced(
    movie: com.example.movieapp.presentation.mapper.MovieUi,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        label = "grid_scale"
    )

    Card(
        modifier = Modifier
            .aspectRatio(0.7f)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .crossfade(300)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .build(),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Icon_color,
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun LoadingGrid() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Icon_color,
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun CompactErrorView(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2D35))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "⚠️", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Icon_color)
            ) {
                Text("Retry")
            }
        }
    }
}