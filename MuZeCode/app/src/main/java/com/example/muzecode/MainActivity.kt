package com.example.muzecode

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import com.example.muzecode.ui.theme.MuZeCodeTheme
import kotlinx.coroutines.delay
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //CREATE MEDIA PLAYER

        val player = ExoPlayer.Builder(this).build()

        val mediaSession = MediaSession.Builder(this, player).build()

        if (Build.VERSION.SDK_INT >= 30) {
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
        }

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
                    )
                    val strings = listOf("Hello", "World", "Jetpack", "Compose", "hello", "Hello", "World", "Jetpack", "Compose", "hello", "Hello", "World", "Jetpack", "Compose", "hello")
                    Greeting(player)
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasFilesPermission() = Environment.isExternalStorageManager()
}

@OptIn(ExperimentalMaterial3Api::class)
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