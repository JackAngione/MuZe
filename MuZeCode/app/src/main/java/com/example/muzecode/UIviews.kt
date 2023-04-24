package com.example.muzecode

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.io.File

class UIviews {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun FolderView(
        player: ExoPlayer,
        playerFunctionality: PlayerFunctionality
    )
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
                                playerFunctionality.playingFolderAudioFiles = playerFunctionality.currentFolderAudioFiles
                                setPlayerQueue(
                                    player = player,
                                    playerFunctionality = playerFunctionality,
                                    index
                                )
                            }
                        }) {
                        Row(modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally))
                        {
                            Text(
                                text = audioFileCard.name,
                                modifier = Modifier.widthIn(max = 320.dp),
                                fontSize = 18.sp,

                                )
                            Spacer(modifier = Modifier.weight(1f))
                            TrackDropDownMenu(player = player, audioCard = audioFileCard)
                        }
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AllTracksView(player: ExoPlayer, playerFunctionality: PlayerFunctionality)
    {
        playerFunctionality.currentFolder = playerFunctionality.musicFolder
        LazyColumn(content =
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
                            onClick = {
                                    setPlayerQueue(
                                        player = player,
                                        playerFunctionality = playerFunctionality,
                                        index
                                    )
                            }) {
                            Row(modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally))
                            {
                                Text(
                                    text = audioFileCard.name,
                                    modifier = Modifier.widthIn(max = 320.dp),
                                    fontSize = 18.sp,

                                )
                                Spacer(modifier = Modifier.weight(1f))
                                TrackDropDownMenu(player = player, audioCard = audioFileCard)
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        )
    }

    @Composable
    fun ServerView(player: ExoPlayer, playerFunctionality: PlayerFunctionality)
    {

    }

    @Composable
    fun TrackDropDownMenu(
        player: ExoPlayer,
        audioCard: File
    )
    {
        var expanded by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Open dropdown menu")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentSize()
            ) {
                Button(onClick = {
                    setNextInQueue(player = player, audioCard = audioCard)
                }) {
                    Text(text = "Add to next in queue")
                }
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

}