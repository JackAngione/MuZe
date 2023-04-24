package com.example.muzecode

import android.service.controls.Control
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.coroutineScope
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
                Text(text = "NAVIGATION DRAWER", modifier = Modifier.padding(10.dp))
                Column() {

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

                        navController.navigate("musicfiles") {

                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        coroutineScope.launch { drawerState.close() }
                    }) {
                        Text(text = "Music Files")
                    }
                }

            }
        },
    ) {
        MainNavigation(player = player, navController = navController, playerFunctionality = playerFunctionality)
        //
    }
}


