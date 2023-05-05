package com.example.muzecode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class Navigation: ViewModel()
{
    @Composable
    fun navDrawerUI(
        database: SongQueueDao,
        playerControls: PlayerControls,
        playerFunctionality: PlayerFunctionality
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
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Menu",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                        //HOME PAGE
                        Button(
                            shape = RoundedCornerShape(10.dp),
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
                        //SONGS PAGE
                        Button(
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
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
                        //SEARCH PAGE
                        Button(
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
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
                        //QUEUE PAGE
                        Button(
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
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
            MainNavigation(database = database, playerControls = playerControls, navController = navController, playerFunctionality = playerFunctionality)
        }
    }
    @Composable
    fun MainNavigation(database: SongQueueDao, playerControls: PlayerControls, navController: NavController, playerFunctionality: PlayerFunctionality) {
        val ui = UIviews()
        NavHost(
            navController = navController as NavHostController,
            startDestination = "home"
        )
        {
            composable("search") { OutlinedTextField(value = "", onValueChange = {}, placeholder = {Text("Implement Search")}) } //should be similar to folderview but with search function
            composable("home") { ui.HomeScreen() }
            composable("folderView") { playerControls.ControlsUI(database = database, playerFunctionality = playerFunctionality) }
            composable("queue") { Text(text = "QUEUE MANAGER") } //should be similar to folderview as well
        }
    }
}

