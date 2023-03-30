package com.example.muzecode

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.provider.Settings.*
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import com.example.muzecode.ui.theme.MuZeCodeTheme
import com.google.accompanist.permissions.*
import kotlinx.coroutines.delay
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //CREATE MEDIA PLAYER

        val player = ExoPlayer.Builder(this).build()

        val mediaSession = MediaSession.Builder(this, player).build()

        /*if (Build.VERSION.SDK_INT >= 30) {
            if (hasFilesPermission()) {
                Toast.makeText(this, R.string.perm_granted, Toast.LENGTH_LONG)
                    .show()
            }
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
            )
        } //should auto grant if <30*/

            //END CREATE MEDIA PLAYER
        setContent {
            MuZeCodeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {/*
                    AndroidView(
                        factory = { context ->
                            val audioFile = File("./Crystal Castles - Leni.mp3")
                            val audioUri = Uri.parse("android.resource://${packageName}/raw/leni")
                            val playerView = PlayerView(context)
                            playerView.player = player
                            //"https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                            player.setMediaItem(MediaItem.fromUri(audioUri))
                            player.prepare()
                            player.play()
                            playerView
                        },
                        update = { playerView ->
                            // Update the view if needed
                        }
                    )*/
                    val strings = listOf("Hello", "World", "Jetpack", "Compose", "hello", "Hello", "World", "Jetpack", "Compose", "hello", "Hello", "World", "Jetpack", "Compose", "hello")
                    permCheck(player)
                }
            }
        }
    }
    /*
    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasFilesPermission() = Environment.isExternalStorageManager()*/
}

/*
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun checkPermission() {
    val storagePermissionState = rememberPermissionState(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    Log.d(null, "Value of storagePermission: ${storagePermissionState.status.isGranted}")
    if(storagePermissionState.status.isGranted) {
        Text("Storage Permission Granted!")
    }else {
        Column {
            val storageError = if(storagePermissionState.status.shouldShowRationale){
                "Storage access is needed for this app. Please grant permission."
            }else {
                "Storage Permission required for the app to function! Please grant the permission."
            }
            Text(storageError)
            Log.d(null, "Should show prompt here...")
            Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}*/

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun permCheck(playerToPass: ExoPlayer) {
    var doNotShow by rememberSaveable{ mutableStateOf(false)}
    val storagePermissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
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
        Greeting(playerToPass)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Greeting(player: ExoPlayer) {
    var isPlaying by remember { mutableStateOf(false) }

    BottomSheetScaffold(

        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        //modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { isPlaying = !isPlaying })
                    {
                        if (!isPlaying) {
                            Text(text = "Play")
                            player.pause()
                        } else {
                            Text(text = "Pause")
                            player.play()
                        }
                    }
                    //AUDIO SEEK BAR
                    var sliderPosition by remember { mutableStateOf(0f) }
                    val duration = player.duration

                    LaunchedEffect(player) {
                        while (true) {
                            sliderPosition = player.currentPosition.toFloat()
                            delay(16)
                        }
                    }

                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = sliderPosition,

                        onValueChange = {
                                newPosition -> player.seekTo(newPosition.toLong())
                        },
                        valueRange =
                            if (duration != C.TIME_UNSET)
                            {
                                0f..duration.toFloat()
                            }
                            else
                            {
                                0f..100f // if audio isn't playing, fallback range
                        },
                    )

                }
            }
        }//end sheet content
    ) {

        /* FUNCTION TO RECURSIVELY GET ALL AUDIO FILES IN A GIVEN FOLDER!!!!!!!!!!!!
        fun getAudioFiles(folder: File): List<File> {
            val audioFiles = mutableListOf<File>()
            folder.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    audioFiles.addAll(getAudioFiles(file))
                } else if (file.isFile && file.extension in arrayOf("mp3", "wav", "ogg", "aac")) {
                    audioFiles.add(file)
                }
            }
            return audioFiles
        } */

        val musicFolder = File("/storage/emulated/0/Music")
        var currentFolder by remember { mutableStateOf(musicFolder) }
        val currentFolderFiles by remember(currentFolder)
        {
            derivedStateOf { currentFolder.listFiles() }
        }

        //if permissions not yet granted, display nothing but prompt?

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item { Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    currentFolder = currentFolder.parentFile
                })
            {
                Text(
                    text = "-Back-",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } }

            items(currentFolderFiles) { audioFile ->
                if(audioFile.extension in arrayOf("mp3", "wav", "ogg", "aac") || audioFile.isDirectory)
                {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if(audioFile.isDirectory)
                            {
                                currentFolder = audioFile
                            }
                            else
                            {
                                val audioUri = Uri.parse(audioFile.toString())
                                player.setMediaItem(MediaItem.fromUri(audioUri))
                                player.prepare()
                            }
                        }) {
                        Text(
                            text = audioFile.name,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }


            }
        }

    }
}