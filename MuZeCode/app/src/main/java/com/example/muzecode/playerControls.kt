package com.example.muzecode

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
class PlayerControls: ViewModel()
{
    private lateinit var player: ExoPlayer


    fun getPlayer(context: Context)
    {
        player = ExoPlayer.Builder(context).build()
        player.playWhenReady = true
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ControlsUI()
    {
        val ui = UIviews()
        val playerFunctionality = remember {
            PlayerFunctionality()
        }
        BottomSheetScaffold(
            sheetContent = {
                var isPlaying by remember { mutableStateOf(true) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    //TOGGLE BETWEEN ALL TRACKS AND FOLDER VIEW
                    Text(text = "TrackView / FolderView")
                    Switch(
                        checked = playerFunctionality.currentView,
                        onCheckedChange = {
                            playerFunctionality.currentView = it

                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
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

                        Button(
                            //modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                player.seekToPreviousMediaItem()
                                if (playerFunctionality.playingSongIndex > 0) {
                                    playerFunctionality.playingSongIndex--
                                }
                                playerFunctionality.playingSong =
                                    playerFunctionality.getCurrentlyPlayingFileName(player).toString()
                            })
                        {
                            Text(text = "<-")
                        }
                        Button(
                            //modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = { isPlaying = !isPlaying })
                        {
                            playerFunctionality.playingSong =
                                playerFunctionality.getCurrentlyPlayingFileName(player).toString()
                            if (!isPlaying) {
                                Text(text = "Play")
                                player.pause()
                            } else {
                                Text(text = "Pause")
                                player.play()
                            }
                        }
                        Button(
                            //modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                player.seekToNextMediaItem()
                                if (playerFunctionality.playingSongIndex < playerFunctionality.currentFolderAudioFiles.size - 1) {
                                    playerFunctionality.playingSongIndex++
                                }
                                playerFunctionality.playingSong =
                                    playerFunctionality.getCurrentlyPlayingFileName(player).toString()
                            })
                        {


                            Text(text = "->")
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
            if(playerFunctionality.currentView)
            {
                ui.FolderView(player = player,  playerFunctionality = playerFunctionality)
            }
            else
            {
                ui.AllTracksView(player = player, playerFunctionality = playerFunctionality)
            }
            //navController.currentDestination
        }

    }
}

