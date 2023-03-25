package com.example.muzecode

import android.app.PendingIntent.getActivity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import com.example.muzecode.ui.theme.MuZeCodeTheme
import java.io.File

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
                    Greeting(strings)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(strings: List<String>) {
    var isPlaying by remember { mutableStateOf(false) }


    BottomSheetScaffold(
        sheetContent = {

            Row() {
                Button(
                    modifier = Modifier,
                    onClick = { isPlaying = !isPlaying})
                {
                    if(!isPlaying)
                    {
                        Text(text = "Play")
                    }
                    else
                    {
                        Text(text = "Pause")
                    }
                }
            }

        }//end sheet content
    ) {


        val audioFolder = File("/storage/emulated/0/Music")
        val audioFiles = audioFolder.listFiles()
        for(audio in audioFiles)
        {
            Log.d("audio", audio.extension)
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(audioFiles) { audioFile ->
                Card(modifier = Modifier.fillMaxWidth(), onClick = { /*TODO*/ }) {
                    Text(
                        text = audioFile.name,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                    )
                }

            }
        }

    }

}


