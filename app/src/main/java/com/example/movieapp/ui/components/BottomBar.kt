package com.example.movieapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.wear.compose.material.Text
import com.example.movieapp.R
import com.example.movieapp.ui.theme.Background_color
import com.example.movieapp.ui.theme.Icon_color

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            title = "Home",
            icon = R.drawable.home,
            route = "home"
        ),
        BottomNavItem(
            title = "Search",
            icon = R.drawable.search_,
            route = "search"
        ),
        BottomNavItem(
            "BookMark",
            R.drawable.path_33968,
            "bookmark"
        ),
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        ) {
            drawRect(color = Icon_color)
        }

        NavigationBar(
            containerColor = Background_color,
            contentColor = Icon_color,
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationRoute!!) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            tint = if (isSelected) Icon_color else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            color = if (isSelected) Icon_color else Color.Gray
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Icon_color,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}



