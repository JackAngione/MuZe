package com.example.muzecode

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
    //navController: NavController,
    playerFunctionality: PlayerFunctionality,
)
{
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        //INSIDE NAV DRAWER
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(text = "NAVIGATION DRAWER")
                Column() {
                    Button(onClick = {

                        navController.navigate("folderView") {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                        coroutineScope.launch { drawerState.close() }
                    }) {
                        Text(text = "nav1")
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
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }

                    }) {
                        Text(text = "nav2")
                    }
                }

            }
        },
        
    ) {
        val ui = UI_views()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") { Text(text = "HOME") }
            composable("folderView") { ui.folderView(
                player = player,
                playerFunctionality = playerFunctionality,
                navController = navController
            ) }
        }
    }
}


