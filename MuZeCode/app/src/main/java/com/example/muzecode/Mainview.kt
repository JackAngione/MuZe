package com.example.muzecode

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import java.io.File
class PlayerFunctionality : ViewModel() {
    var currentView by mutableStateOf(false)
    val musicFolder = File("/storage/emulated/0/Music")
    var currentFolder by mutableStateOf(musicFolder)
    val currentFolderFiles by derivedStateOf { currentFolder.listFiles() }

    val currentFolderAudioFiles by derivedStateOf {
        currentFolder.listFiles().filter { file ->
            file.isFile && file.extension in arrayOf("mp3", "wav", "ogg", "aac")
        }.sortedBy { it.name }
    }
    var playingFolderAudioFiles by  mutableStateOf(currentFolderAudioFiles)
    var playingSongIndex by  mutableStateOf(0)

    var playingSong by mutableStateOf("")

}
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun  getCurrentlyPlayingFileName(exoPlayer: ExoPlayer): String? {
        val mediaItem = exoPlayer.currentMediaItem ?: return ""
        val uri = mediaItem.playbackProperties?.uri ?: return ""

        return uri.path?.let { File(it).name }
    }

    fun setPlayerQueue (
        player: ExoPlayer,
        playerFunctionality: PlayerFunctionality,
        selectedIndex: Int
    )
    {
        //playerFunctionality.playingFolderAudioFiles = playerFunctionality.currentFolderAudioFiles
        player.clearMediaItems()
        playerFunctionality.playingSongIndex = 0
        val mediaItems = mutableListOf<MediaItem>()
        for(i in playerFunctionality.playingFolderAudioFiles.indices)
        {
            if(playerFunctionality.playingFolderAudioFiles[i].isDirectory || playerFunctionality.playingFolderAudioFiles[i].extension !in arrayOf("mp3", "wav", "ogg", "aac") )
            {
                continue
            }
            else
            {
                val audioUri = Uri.parse(playerFunctionality.playingFolderAudioFiles[i].toString())
                mediaItems.add(MediaItem.fromUri(audioUri))
            }
        }
        player.setMediaItems(mediaItems)
        player.prepare()
        while(playerFunctionality.playingSongIndex < selectedIndex)
        {
            player.seekToNextMediaItem()
            playerFunctionality.playingSongIndex++
        }
        playerFunctionality.playingSong = getCurrentlyPlayingFileName(player).toString()
    }



