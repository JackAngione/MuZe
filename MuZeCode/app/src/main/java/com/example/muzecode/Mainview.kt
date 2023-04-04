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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.io.File

class Mainview {
    @OptIn(ExperimentalMaterial3Api::class)
    @ExperimentalComposeUiApi
    @androidx.media3.common.util.UnstableApi
    @ExperimentalFoundationApi
    @Composable
    fun  FolderView(player: ExoPlayer, navController: NavController) {

        var isPlaying by remember { mutableStateOf(false) }
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
        var playingSongIndex by remember{mutableStateOf(0)}

        var playingSong by remember {
            mutableStateOf("")
        }
        fun getCurrentlyPlayingFileName(exoPlayer: ExoPlayer): String? {
            val mediaItem = exoPlayer.currentMediaItem ?: return null
            val uri = mediaItem.playbackProperties?.uri ?: return null

            return uri.path?.let { File(it).name }
        }

        BottomSheetScaffold(

            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = playingSong,
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
                                if(playingSongIndex>0)
                                {
                                    playingSongIndex--
                                }
                                playingSong = getCurrentlyPlayingFileName(player).toString()
                            })
                        {
                            Text(text = "<-")
                        }
                        Button(
                            //modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = { isPlaying = !isPlaying })
                        {
                            playingSong = getCurrentlyPlayingFileName(player).toString()
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
                                if(playingSongIndex<currentFolderAudioFiles.size-1)
                                {
                                    playingSongIndex++
                                }
                                playingSong = getCurrentlyPlayingFileName(player).toString()
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

                        onValueChange = {
                                newPosition -> player.seekTo(newPosition.toLong())
                        },
                        valueRange =
                        if (duration != C.TIME_UNSET)
                        {
                            0f..duration.toFloat()
                        }
                        else
                        {
                            0f..100f // if audio isn't playing, fallback range
                        },
                    )
                }
            }//end sheet content
        ) {

            fun setPlayerQueue(selectedIndex: Int)
            {
                playingFolderAudioFiles = currentFolderAudioFiles
                player.clearMediaItems()
                playingSongIndex = 0
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
                while(playingSongIndex < selectedIndex)
                {
                    player.seekToNextMediaItem()
                    playingSongIndex++
                }
                playingSong = getCurrentlyPlayingFileName(player).toString()

            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(currentFolder != File("/storage/emulated/0/Music")) {
                            currentFolder = currentFolder.parentFile as File
                            playingSongIndex = 0
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