package com.example.muzecode

import android.media.session.MediaController
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.muzecode.ui.theme.MuZeCodeTheme

class MainActivity : ComponentActivity() {
    private lateinit var mediaController: MediaController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuZeCodeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    val songslll = listOf(
                        Song("Song 1", "Artist 1"),
                        Song("Song 2", "Artist 2"),
                        Song("Song 3", "Artist 3"),
                        Song("Song 4", "Artist 4"),
                        Song("Song 5", "Artist 5")
                    )
                    SongList(songslll)
                }
            }
        }
    }
}
data class Song(val title: String, val artist: String)
@Composable
fun SongList(songsList: List<Song>)
{
    LazyColumn(modifier = Modifier.padding(16.dp))
    {
        items(songsList) { song ->
            Text(text = "${song.title} - ${song.artist}")
        }
    }
}