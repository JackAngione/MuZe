package com.example.muzecode

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerControls(): ViewModel()
{
    private lateinit var player: ExoPlayer
    private lateinit var notificationManager: MediaNotificationManager

    @SuppressLint("CoroutineCreationDuringComposition")
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setPlayer(context: Context, database: SongQueueDao, playerFunctionality: PlayerFunctionality)
    {
        player = ExoPlayer.Builder(context).build()
        
        val sessionActivityPendingIntent =
            context.packageManager?.getLaunchIntentForPackage(context.packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(context, 0, sessionIntent, PendingIntent.FLAG_IMMUTABLE)
            }
        val mediaSession = MediaSession.Builder(context, player)
            .setSessionActivity(sessionActivityPendingIntent!!).build()

        notificationManager =
            MediaNotificationManager(
                context,
                mediaSession.token,
                player,
                PlayerNotificationListener()
            )
        viewModelScope.launch {
            playerFunctionality.startUpQueue(player = player, playerFunctionality = playerFunctionality)

        }
    }
    @UnstableApi private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ControlsUI(playerFunctionality: PlayerFunctionality, database: SongQueueDao)
    {
        val ui = UIviews()

        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row() {
                        //TOGGLE BETWEEN ALL TRACKS AND FOLDER VIEW
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "TrackView / FolderView")
                            Switch(
                                checked = playerFunctionality.trackFolderToggle,
                                onCheckedChange = {
                                    playerFunctionality.trackFolderToggle = it

                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.tertiary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        //REPEAT BUTTON
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Repeat")
                            Button(shape = RoundedCornerShape(10.dp), onClick = {
                                when (player.repeatMode) {
                                    0 -> {
                                        player.repeatMode = Player.REPEAT_MODE_ONE
                                    }
                                    1 -> {
                                        player.repeatMode = Player.REPEAT_MODE_ALL
                                    }
                                    2 -> {
                                        player.repeatMode = Player.REPEAT_MODE_OFF
                                    }
                                }

                            }) {
                                when (player.repeatMode) {
                                    0 -> {
                                        Text(text = "off", color = MaterialTheme.colorScheme.tertiary)
                                    }
                                    1 -> {
                                        Text(text = "single", color = MaterialTheme.colorScheme.tertiary)
                                    }
                                    2 -> {
                                        Text(text = "all", color = MaterialTheme.colorScheme.tertiary)
                                    }
                                }


                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        //SHUFFLE
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Shuffle")
                            Switch(
                                checked = player.shuffleModeEnabled,
                                onCheckedChange = {
                                    player.shuffleModeEnabled = !player.shuffleModeEnabled
                                })
                        }
                    }

                    Text(
                        text = playerFunctionality.playingSong,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 10.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        //Previous Song Button
                        IconButton(
                            onClick = {
                                player.seekToPreviousMediaItem()
                                if (playerFunctionality.playingSongIndex > 0) {
                                    playerFunctionality.playingSongIndex--
                                }
                                playerFunctionality.playingSong =
                                    playerFunctionality.getCurrentlyPlayingFileName(player).toString()
                            })
                        {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Play")
                        }
                        //PLAY/PAUSE BUTTON
                        IconButton(
                            onClick = {
                                if (player.isPlaying)
                                {
                                    player.pause()
                                }
                                else
                                {
                                player.play()
                                }
                            })
                        {
                            playerFunctionality.playingSong =
                                playerFunctionality.getCurrentlyPlayingFileName(player).toString()
                            if (!player.isPlaying) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
                            } else {
                                Icon(Icons.Filled.Pause, contentDescription = "Play")
                            }

                        }
                        //NEXT SONG BUTTON
                        IconButton(
                            onClick = {
                                player.seekToNextMediaItem()
                                if (playerFunctionality.playingSongIndex < playerFunctionality.currentFolderAudioFiles.size - 1) {
                                    playerFunctionality.playingSongIndex++
                                }
                                playerFunctionality.playingSong =
                                    playerFunctionality.getCurrentlyPlayingFileName(player).toString()
                            })
                        {
                            Icon(Icons.Filled.ArrowForward, contentDescription = "Play")
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
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.tertiary
                        ),
                        value = sliderPosition,

                        onValueChange = { newPosition ->
                            player.seekTo(newPosition.toLong())
                        },
                        valueRange =
                        if (duration != C.TIME_UNSET) {
                            0f..duration.toFloat()
                        } else {
                            0f..100f // if audio isn't playing, fallback range
                        },
                    )
                }
            }
        )

        //BACKGROUND CONTENT
        {
            LaunchedEffect(playerFunctionality.playingSong) {
                val currentlyPlayingPath: String? = playerFunctionality.getCurrentlyPlayingFilePath(player)
                if(currentlyPlayingPath?.startsWith("/storage/emulated/0/") == true)
                {

                    val padding = "yyy"
                    val filePathforUpdate = "$padding$currentlyPlayingPath"
                    val queueRowCount: Int = withContext(Dispatchers.IO)
                    {
                        database.songQueueRowCount()
                    }
                    //database queue is not empty
                    if(queueRowCount != 0)
                    {
                        val firstRow = withContext(Dispatchers.IO)
                        {
                            database.getFirstRow()
                        }
                        //firstRow.songUri = playerFunctionality.getCurrentlyPlayingFilePath(playerControls.getPlayer()).toString()
                        val updatedEntity = withContext(Dispatchers.IO)
                        {
                            database.updateSongRow(newName =filePathforUpdate)
                        }
                    }
                }
            }
            if(playerFunctionality.trackFolderToggle)
            {
                ui.FolderView(player = player,  playerFunctionality = playerFunctionality)
            }
            else
            {
                ui.AllTracksView(player = player, playerFunctionality = playerFunctionality)
            }

        }
    }
}

