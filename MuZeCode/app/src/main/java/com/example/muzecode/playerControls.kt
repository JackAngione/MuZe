package com.example.muzecode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ControlsUI(
        player: ExoPlayer,
        playerFunctionality: PlayerFunctionality
        )
    {
        BottomSheetScaffold(
            sheetContent = {
                var isPlaying by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
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
                                    getCurrentlyPlayingFileName(player).toString()
                            })
                        {
                            Text(text = "<-")
                        }
                        Button(
                            //modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = { isPlaying = !isPlaying })
                        {
                            playerFunctionality.playingSong =
                                getCurrentlyPlayingFileName(player).toString()
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
                                    getCurrentlyPlayingFileName(player).toString()
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
            //navController.currentDestination
        }

    }