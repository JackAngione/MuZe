package com.example.muzecode

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavigation(player: ExoPlayer, navController: NavController,  playerFunctionality: PlayerFunctionality) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = "home"
    )
    {
        composable("search") { OutlinedTextField(value = "", onValueChange = {}, placeholder = {Text("Implement Search")}) } //should be similar to folderview but with search function
        composable("home") { Text(text = "HOME") }
        composable("folderView") { ControlsUI(player = player) }
        composable("queue") { Text(text = "QUEUE MANAGER") } //should be similar to folderview as well
    }
}