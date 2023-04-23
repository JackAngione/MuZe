package com.example.muzecode

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.muzecode.ui.theme.BlueGrey40
import com.example.muzecode.ui.theme.MidGrey40
import com.example.muzecode.ui.theme.MuZeCodeTheme
import kotlinx.coroutines.delay
import java.io.File

class UI_views {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun folderView(
        player: ExoPlayer,
        playerFunctionality: PlayerFunctionality
    )
    {
        /*
        val playerFunctionality = remember {

            PlayerFunctionality()
        }
        */
            //START FOLDER LIST UI
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {


                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.background
                        ),
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
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.background
                            ),
                            onClick = {

                                if (audioFileCard.isDirectory) {
                                    playerFunctionality.currentFolder = audioFileCard
                                } else {
                                    playerFunctionality.playingFolderAudioFiles = playerFunctionality.currentFolderAudioFiles
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
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.background
                            ),
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
    private fun getAudioFiles(folder: File): List<File> {
        val audioFiles = mutableListOf<File>()
        folder.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                audioFiles.addAll(getAudioFiles(file))
            } else if (file.isFile && file.extension in arrayOf("mp3", "wav", "ogg", "aac")) {
                audioFiles.add(file)
            }
        }
        return audioFiles
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun allTracksView(player: ExoPlayer, playerFunctionality: PlayerFunctionality)
    {
        playerFunctionality.currentFolder = playerFunctionality.musicFolder
        LazyColumn(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            content =
            {
                playerFunctionality.playingFolderAudioFiles = getAudioFiles(playerFunctionality.currentFolder)
                itemsIndexed(playerFunctionality.playingFolderAudioFiles) { index, audioFileCard ->
                    if (audioFileCard.extension in arrayOf(
                            "mp3",
                            "wav",
                            "ogg",
                            "aac"
                        ) || audioFileCard.isDirectory
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.background
                            ),
                            onClick = {
                                    setPlayerQueue(
                                        player = player,
                                        playerFunctionality = playerFunctionality,
                                        index
                                    )

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
        )

    }
}