package com.example.muzecode

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavigation(player: ExoPlayer, navController: NavController,  playerFunctionality: PlayerFunctionality) {
    val ui = UI_views()
    NavHost(
        navController = navController as NavHostController,
        startDestination = "home"
    )
    {
        composable("search") { Text(text = "SEARCH BAR & RESULTS") }
        composable("home") { Text(text = "HOME") }
        composable("folderView") { ControlsUI(player = player) }
    }
}