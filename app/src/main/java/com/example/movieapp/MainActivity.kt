package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapp.ui.screens.HomeScreen
import com.example.movieapp.ui.theme.MovieAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.data.BookmarkRepository
import com.example.movieapp.data.local.MovieDatabase
import com.example.movieapp.ui.components.BottomBar
import com.example.movieapp.ui.screens.BookmarkScreen
import com.example.movieapp.ui.screens.DetailsScreen
import com.example.movieapp.ui.screens.SearchScreen
import com.example.movieapp.ui.theme.Background_color
import com.example.movieapp.viewmodel.BookmarkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bookmarkViewModel: BookmarkViewModel = hiltViewModel()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        containerColor = Background_color
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController)
            }

            composable("search") {
                SearchScreen(navController = navController)
            }

            composable("details/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toInt() ?: return@composable
                DetailsScreen(
                    movieId = movieId,
                    navController = navController,
                    bookmarkViewModel = bookmarkViewModel
                )
            }
            composable("bookmark") {
                BookmarkScreen(navController = navController)
            }
        }
    }
}
