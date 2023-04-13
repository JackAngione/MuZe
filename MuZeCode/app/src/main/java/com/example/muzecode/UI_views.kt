package com.example.muzecode

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.io.File

class UI_views {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
        ExperimentalFoundationApi::class
    )
    @Composable
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun folderView(
        player: ExoPlayer,

        playerFunctionality: PlayerFunctionality,
        navController: NavController
    )
    {
        BottomSheetScaffold(
            sheetContent = {
                Text(text = "CONTROLS UI")

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
            //START FOLDER LIST UI
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (playerFunctionality.currentFolder != File("/storage/emulated/0/Music")) {
                                playerFunctionality.currentFolder =
                                    playerFunctionality.currentFolder.parentFile as File
                                playerFunctionality.playingSongIndex = 0
                            }
                        })
                    {
                        Text(
                            text = "-Back-",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                itemsIndexed(playerFunctionality.currentFolderAudioFiles) { index, audioFileCard ->
                    if (audioFileCard.extension in arrayOf(
                            "mp3",
                            "wav",
                            "ogg",
                            "aac"
                        ) || audioFileCard.isDirectory
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {

                                if (audioFileCard.isDirectory) {
                                    playerFunctionality.currentFolder = audioFileCard
                                } else {
                                    setPlayerQueue(
                                        player = player,
                                        playerFunctionality = playerFunctionality,
                                        index
                                    )
                                }
                            }) {
                            Text(
                                text = audioFileCard.name,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
                itemsIndexed(playerFunctionality.currentFolderFiles) { index, audioFileCard ->
                    if (audioFileCard.isDirectory) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                playerFunctionality.currentFolder = audioFileCard

                            }) {
                            Text(
                                text = audioFileCard.name,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }

            }
        }
        //ControlsUI(player = player, playerFunctionality = playerFunctionality, navController = navController)
    }
}