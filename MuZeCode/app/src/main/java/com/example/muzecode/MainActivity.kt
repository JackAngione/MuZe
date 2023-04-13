package com.example.muzecode

import android.net.Uri
import android.os.Bundle
import android.provider.Settings.*
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.navigation.compose.*
import com.example.muzecode.ui.theme.MuZeCodeTheme
import com.google.accompanist.permissions.*

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //CREATE MEDIA PLAYER
        val player = ExoPlayer.Builder(this).build()
        val mediaSession = MediaSession.Builder(this, player).build()

        //END CREATE MEDIA PLAYER
        setContent {
            MuZeCodeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //RUN PERMISSIONS CHECK
                    PermCheck(playerToPass = player)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun  PermCheck(playerToPass: ExoPlayer) {
    var doNotShow by rememberSaveable{ mutableStateOf(false)}
    val storagePermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    val context = LocalContext.current
    PermissionRequired(
        permissionState = storagePermissionState,
        permissionNotGrantedContent = {
            //Runs if the specified permission is not granted
            if(doNotShow){
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "MuZe unavailable!",
                        fontSize = 26.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = "Please grant storage permission in settings",
                        fontSize = 26.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    Button(onClick = { //if cancel is pressed, nothing is generated but a button to go to settings
                        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                        val i = Intent(ACTION_APPLICATION_DETAILS_SETTINGS,uri)
                        context.startActivity(i) },
                        modifier = Modifier.padding(8.dp)
                    ){
                        Text("Go to MuZe Settings")
                    }
                }
            } else {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MuZe makes use of your device's internal storage.",
                        fontSize = 26.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = "Grant permission?",
                        fontSize = 26.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    Row {//creates a pop-up that automatically edits storage permission
                        Button(onClick = {
                            storagePermissionState.launchPermissionRequest()},
                            modifier = Modifier.padding(8.dp)
                        ){
                            Text("Okay")
                        }
                        Button(
                            onClick = {doNotShow = true},
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        },
        permissionNotAvailableContent = { //shouldn't be an issue but is a failsafe for unavailable permission
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text( "Storage Access Denied! Please grant access via app settings!")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { //onClick sends to the app's permission pages
                    val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                    val i = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,uri)
                    context.startActivity(i)
                }){
                    Text("Go to MuZe Settings")
                }
            }
        }
    ) { //will run once permission is granted

        val ui = UI_views()
        val playerFunctionality = PlayerFunctionality()
        /*
        //NAVIGATION
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") { Text(text = "HOME") }
            composable("folderView") { ui.folderView(
                player = playerToPass,
                playerFunctionality = playerFunctionality,
                navController = navController
            ) }
        }

         */
        //navController.navigate("home")

        navDrawerUI(playerToPass, playerFunctionality)

    }
}