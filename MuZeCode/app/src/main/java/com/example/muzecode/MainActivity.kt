package com.example.muzecode
import com.example.muzecode.Mainview
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
                            val audioUri = Uri.parse("android.resource://${packageName}/raw/leni")
                            val playerView = PlayerView(context)
                            playerView.player = player
                            player.setMediaItem(MediaItem.fromUri(audioUri))
                            player.prepare()
                            player.play()
                            playerView
                        },
                        update = { playerView ->
                            // Update the view if needed
                        }
                    )
                    //LOAD UP MAIN UI VIEW
                    val mainview = Mainview()
                    mainview.FolderView(player)
                }
            }
        }
    }
}


