package com.example.muzecode

import android.net.Uri
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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import java.io.File

class Mainview {

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun  getCurrentlyPlayingFileName(exoPlayer: ExoPlayer): String? {
        val mediaItem = exoPlayer.currentMediaItem ?: return ""
        val uri = mediaItem.playbackProperties?.uri ?: return ""

        return uri.path?.let { File(it).name }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @ExperimentalComposeUiApi
    @androidx.media3.common.util.UnstableApi
    @ExperimentalFoundationApi
    @Composable
    fun  FolderView(player: ExoPlayer, navController: NavController) {


        val musicFolder = File("/storage/emulated/0/Music")
        var currentFolder by remember { mutableStateOf(musicFolder) }
        val currentFolderFiles by remember(currentFolder)
        {
            derivedStateOf { currentFolder.listFiles() }
        }
        val currentFolderAudioFiles by remember(currentFolder.listFiles()){
            derivedStateOf { currentFolder.listFiles().filter { file ->
                file.isFile && file.extension in arrayOf("mp3", "wav", "ogg", "aac")
            }.sortedBy {it.name} }
        }
        var playingFolderAudioFiles by remember{ mutableStateOf(currentFolderAudioFiles) }
        val playingSongIndex = remember{mutableStateOf(0)}

        val playingSong = remember {
            mutableStateOf("")
        }


        BottomSheetScaffold(

            sheetContent = {
                val playerControls = PlayerControls()
                playerControls.ControlsUI(player = player, playingSong = playingSong, playingSongIndex = playingSongIndex, currentFolderAudioFiles = currentFolderAudioFiles)
            }//end sheet content
        ) {

            fun setPlayerQueue(selectedIndex: Int)
            {
                playingFolderAudioFiles = currentFolderAudioFiles
                player.clearMediaItems()
                playingSongIndex.value = 0
                val mediaItems = mutableListOf<MediaItem>()
                for(i in playingFolderAudioFiles.indices)
                {
                    if(playingFolderAudioFiles[i].isDirectory || playingFolderAudioFiles[i].extension !in arrayOf("mp3", "wav", "ogg", "aac") )
                    {
                        continue
                    }
                    else
                    {
                        val audioUri = Uri.parse(playingFolderAudioFiles[i].toString())
                        mediaItems.add(MediaItem.fromUri(audioUri))
                    }
                }
                player.setMediaItems(mediaItems)
                player.prepare()
                while(playingSongIndex.value < selectedIndex)
                {
                    player.seekToNextMediaItem()
                    playingSongIndex.value++
                }
                playingSong.value = getCurrentlyPlayingFileName(player).toString()

            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(currentFolder != File("/storage/emulated/0/Music")) {
                            currentFolder = currentFolder.parentFile as File
                            playingSongIndex.value = 0
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
                } }

                itemsIndexed(currentFolderAudioFiles) {index, audioFileCard ->
                    if(audioFileCard.extension in arrayOf("mp3", "wav", "ogg", "aac") || audioFileCard.isDirectory)
                    {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {

                                if(audioFileCard.isDirectory)
                                {
                                    currentFolder = audioFileCard
                                }
                                else
                                {
                                    setPlayerQueue(index)
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
                itemsIndexed(currentFolderFiles) {index, audioFileCard ->
                    if(audioFileCard.isDirectory)
                    {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                currentFolder = audioFileCard

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

    }


}