package com.example.muzecode

import android.service.controls.Control
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun navDrawerUI(
    player: ExoPlayer,
)
{
    val playerFunctionality = PlayerFunctionality()
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        //INSIDE NAV DRAWER
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(text = "MuZe Menu")
                Column() {
                    Button(onClick = {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate("search")
                            {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                        }) {
                        Text(text = "Search")
                    }
                    Button(onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("folderView") {

                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    }) {
                        Text(text = "Songs")
                    }
                    Button(
                        onClick =
                        {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate("home")
                            {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                    }) {
                        Text(text = "Home")
                    }
                    Button(onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("queue")
                        {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }

                    }) {
                        Text(text = "Queue")
                    }
                }

            }
        },
    ) {
        MainNavigation(player = player, navController = navController, playerFunctionality = playerFunctionality)
    }
}