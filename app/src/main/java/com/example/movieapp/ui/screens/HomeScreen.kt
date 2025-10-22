package com.example.movieapp.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.movieapp.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.data.model.MovieDetails
import com.example.movieapp.viewmodel.MovieViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment


@Composable
fun HomeScreen(navController: NavController, viewModel: MovieViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Now Playing") }
    val nowPlaying by viewModel.nowPlaying.collectAsState()
    val upcoming by viewModel.upcoming.collectAsState()
    val topRated by viewModel.topRated.collectAsState()
    val popular by viewModel.popular.collectAsState()

    val baseList = when (selectedTab) {
        "Now Playing" -> nowPlaying
        "Upcoming" -> upcoming
        "Top Rated" -> topRated
        "Popular" -> popular
        else -> nowPlaying
    }

    val filteredMovies = if (searchQuery.isNotBlank()) {
        baseList.filter { movie ->
            movie.title.contains(searchQuery, ignoreCase = true)
        }
    } else {
        baseList
    }


    Scaffold(
        containerColor = Background_color,
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            Column {
                Text(
                    text = "What do you want to watch?",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Normal,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp)
                )
                Spacer(Modifier.height(16.dp))
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Popular Movies
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(popular) { movie ->
                    MovieItem(
                        movieDetails = movie,
                        onClick = { navController.navigate("details/${movie.id}")}


                    )


                }
            }

            Spacer(Modifier.height(24.dp))

            // Tabs (Modified)
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                val tabs = listOf("Now Playing", "Upcoming", "Top Rated", "Popular")
                tabs.forEach { tab ->
                    Column(
                        modifier = Modifier
                            .clickable { selectedTab = tab },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = if (tab == selectedTab) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(24.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (tab == selectedTab) Color.Gray else Color.Transparent)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Lazy Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMovies) { movie ->
                    MovieGridItem(
                        movieDetails = movie,
                        onClick = {navController.navigate(("details/${movie.id}"))}
                    )

                }
            }
        }
    }
}


@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search",
                color = Color.Gray,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                modifier = Modifier.padding(start = 8.dp),
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF67686D),
            unfocusedContainerColor = Color(0xFF67686D),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 16.dp)

    )
}

@Composable
fun MovieItem(movieDetails: MovieDetails, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(265.dp)
            .padding(8.dp)
            .background(Background_color)
            .clickable { onClick() },

        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Background_color),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            val imageUrl = "https:image.tmdb.org/t/p/w500${movieDetails.posterPath}"
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = movieDetails.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(265.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = movieDetails.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${movieDetails.voteAverage}/10",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun MovieGridItem(movieDetails: MovieDetails,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(150.dp)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        val imageUrl = "https:image.tmdb.org/t/p/w500${movieDetails.posterPath}"
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = movieDetails.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
