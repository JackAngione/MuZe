package com.example.muzecode

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
class PlayerFunctionality(val database: SongQueueDao): ViewModel() {
    //0 is track view, 1 is folder view, 2 is server view
    var trackFolderToggle by mutableStateOf(false)

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

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun  getCurrentlyPlayingFileName(exoPlayer: ExoPlayer): String? {
        val mediaItem = exoPlayer.currentMediaItem ?: return ""
        val uri = mediaItem.playbackProperties?.uri ?: return ""

        return uri.path?.let { File(it).name }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun  getCurrentlyPlayingFilePath(exoPlayer: ExoPlayer): String? {
        val mediaItem = exoPlayer.currentMediaItem ?: return ""
        val uri = mediaItem.playbackProperties?.uri ?: return ""

        return uri.path
    }
    //upon app startup, replace current queue with previously closed queue
    suspend fun startUpQueue(
        player: ExoPlayer,
        playerFunctionality: PlayerFunctionality,
    )
    {
        player.clearMediaItems()
        val queueRowCount: Int = withContext(Dispatchers.IO)
        {
            database.songQueueRowCount()
        }
        //database queue is not empty
        if(queueRowCount != 0)
        {
            val mediaItems = mutableListOf<MediaItem>()
            val selectedIndex: Song = withContext(Dispatchers.IO)
            {
                database.getFirstRow()
            }

            var songQueue: List<String> = withContext(Dispatchers.IO)
            {
                database.getAll()
            }
            songQueue = songQueue.drop(1)
            for(i in songQueue)
            {
                mediaItems.add(MediaItem.fromUri(i.toUri()))
            }
            player.setMediaItems(mediaItems)
            player.prepare()
            //SKIP THROUGH QUEUE TO GET TO DESIRED INDEX
            while(getCurrentlyPlayingFilePath(player).toString() != selectedIndex.songUri.drop(3))
            {
                player.seekToNextMediaItem()
                //playerFunctionality.playingSongIndex++
            }
            playerFunctionality.playingSong = getCurrentlyPlayingFileName(player).toString()
        }
    }

    suspend fun setPlayerQueue (
        player: ExoPlayer,
        playerFunctionality: PlayerFunctionality,
        selectedIndex: Int
    )
    {
        val mediaItems = mutableListOf<MediaItem>()
        player.clearMediaItems()
        withContext(Dispatchers.IO)
        {
            database.deleteAllQueue()
            playerFunctionality.playingSongIndex = 0

            database.insertSongUri(Song(songUri = selectedIndex.toString()))

            for (i in playerFunctionality.playingFolderAudioFiles.indices) {
                if (playerFunctionality.playingFolderAudioFiles[i].isDirectory || playerFunctionality.playingFolderAudioFiles[i].extension !in arrayOf(
                        "mp3",
                        "wav",
                        "ogg",
                        "aac"
                    )
                ) {
                    continue
                } else {
                    val audioUri =
                        Uri.parse(playerFunctionality.playingFolderAudioFiles[i].toString())

                    database.insertSongUri(Song(songUri = audioUri.toString()))

                    mediaItems.add(MediaItem.fromUri(audioUri))
                }
            }
        }
        player.setMediaItems(mediaItems)
        player.prepare()
        //SKIP THROUGH QUEUE TO GET TO DESIRED INDEX
        while(playerFunctionality.playingSongIndex < selectedIndex)
        {
            player.seekToNextMediaItem()
            playerFunctionality.playingSongIndex++
        }
        playerFunctionality.playingSong = getCurrentlyPlayingFileName(player).toString()
    }
    fun setNextInQueue(player: ExoPlayer, audioCard: File)
    {
        val currentIndex = player.currentMediaItemIndex
        val audioUri = Uri.parse(audioCard.toString())
        player.addMediaItem(currentIndex+1, MediaItem.fromUri(audioUri))
    }

}
