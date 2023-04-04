package com.example.muzecode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import java.io.File

class PlayerControls {

    @Composable
    fun ControlsUI(player: ExoPlayer, playingSong: MutableState<String>, playingSongIndex: MutableState<Int>, currentFolderAudioFiles: List<File>)
    {
        val folderview = Mainview()

        var isPlaying by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = playingSong.value,
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
                        if(playingSongIndex.value>0)
                        {
                            playingSongIndex.value--
                        }
                        playingSong.value = folderview.getCurrentlyPlayingFileName(player).toString()
                    })
                {
                    Text(text = "<-")
                }
                Button(
                    //modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { isPlaying = !isPlaying })
                {
                    playingSong.value = folderview.getCurrentlyPlayingFileName(player).toString()
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
                        if(playingSongIndex.value<currentFolderAudioFiles.size-1)
                        {
                            playingSongIndex.value++
                        }
                        playingSong.value = folderview.getCurrentlyPlayingFileName(player).toString()
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
    }


}