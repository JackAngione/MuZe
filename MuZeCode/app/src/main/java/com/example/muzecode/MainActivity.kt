package com.example.muzecode
import com.example.muzecode.Mainview
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.Manifest
import android.content.Intent
import android.provider.ContactsContract
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.muzecode.ui.theme.MuZeCodeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {

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
                    AndroidView(
                        factory = { context ->
                            val playerView = PlayerView(context)
                            playerView.player = player
                            /*
                            val audioUri = Uri.parse("android.resource://${packageName}/raw/leni")
                            //player.setMediaItem(MediaItem.fromUri(audioUri))
                            //player.prepare()
                            //player.play()
                             */
                            playerView
                        },
                        update = { playerView ->
                            // Update the view if needed
                        }
                    )
                    //RUN PERMISSIONS CHECK
                    permCheck(playerToPass = player)
                }
            }
        }

    }
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun permCheck(playerToPass: ExoPlayer) {
        var doNotShow by rememberSaveable{ mutableStateOf(false)}
        val storagePermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
        val context = LocalContext.current
        PermissionRequired(
            permissionState = storagePermissionState,
            permissionNotGrantedContent = {
                //Runs if the specified permission is not granted
                if(doNotShow){
                    Text("App Unavailable")
                } else {
                    Column {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "The App makes use of your device's internal storage. Grant permission?"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {//creates a pop-up that automatically edits storage permission
                            Button(onClick = {storagePermissionState.launchPermissionRequest()}){
                                Text("OK")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {doNotShow = true}) {
                                Text("No Thanks")
                            }
                        }
                    }
                }
            },
            permissionNotAvailableContent = { //shouldn't be an issue but is a failsafe for unavailable permission
                Column {
                    Text( "Storage Access Denied! Please grant access via app settings!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val i = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(i)
                    }){
                        Text("Go to Settings")
                    }
                }
            }
        ) { //will run once permission is granted
            //LOAD UP MAIN UI VIEW
            val mainview = Mainview()

            //NAVIGATION
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { mainview.FolderView(playerToPass, navController = navController) }
            }
            navController.navigate("home")
        }
    }
}


