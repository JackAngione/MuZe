package com.example.muzecode

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.SubcomposeAsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.UnknownHostException
import kotlin.random.Random


class UIviews: ViewModel() {
    data class albumArt(
        val id: String,
        val author: String,
        val width: Int,
        val height: Int,
        val url: String,
        val download_url: String,
    )

    @Composable
    fun HomeScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MuZe",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
                //modifier = Modifier.size(40.dp)
            )
            Text(text = "Jack Angione", textAlign = TextAlign.Center)
            Text(text = "Pualo Vallecillo Rangel", textAlign = TextAlign.Center)
            Text(text = "Ayoob Mohammed Redi", textAlign = TextAlign.Center)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun FolderView(
        player: ExoPlayer,
        playerFunctionality: PlayerFunctionality
    ) {
        val coroutineScope = rememberCoroutineScope()

        //ALBUM ART
        val albumArtList = remember { mutableStateOf<List<albumArt>>(emptyList()) }
        LaunchedEffect(playerFunctionality.currentFolder)
        {
            coroutineScope.launch {
                try {
                    albumArtList.value =
                        httpGet(trackCount = playerFunctionality.currentFolderAudioFiles.size)
                } catch (e: UnknownHostException) {
                    albumArtList.value = emptyList()
                }
            }
        }
        //START FOLDER LIST UI
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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
                        "aac",
                        "flac"
                    ) || audioFileCard.isDirectory
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = {

                            if (audioFileCard.isDirectory) {
                                playerFunctionality.currentFolder = audioFileCard
                            } else {
                                playerFunctionality.playingFolderAudioFiles =
                                    playerFunctionality.currentFolderAudioFiles
                                viewModelScope.launch {
                                    playerFunctionality.setPlayerQueue(
                                        player = player,
                                        playerFunctionality = playerFunctionality,
                                        selectedIndex = index
                                    )
                                }
                            }
                        }) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                //.border(2.dp, Color.Red)
                            )
                            {
                                if (albumArtList.value.size >= playerFunctionality.currentFolderAudioFiles.size && albumArtList.value.isNotEmpty()) {
                                    SubcomposeAsyncImage(
                                        model = albumArtList.value.elementAt(index).download_url,
                                        loading = {
                                            CircularProgressIndicator()
                                        },
                                        contentDescription = "downloadedimage"
                                    )
                                }
                            }
                            Text(
                                text = audioFileCard.name,
                                modifier = Modifier
                                    .width(300.dp)
                                    .padding(10.dp),
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TrackDropDownMenu(
                                playerFunctionality = playerFunctionality,
                                player = player,
                                audioCard = audioFileCard
                            )
                        }
                    }
                }
            }
            itemsIndexed(playerFunctionality.currentFolderFiles) { index, audioFileCard ->
                if (audioFileCard.isDirectory) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = {
                            playerFunctionality.currentFolder = audioFileCard
                        }) {
                        Text(
                            text = audioFileCard.name,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.onSurface
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
    fun AllTracksView(player: ExoPlayer, playerFunctionality: PlayerFunctionality) {
        //ALBUM ART
        val coroutineScope = rememberCoroutineScope()
        val albumArtList = remember { mutableStateOf<List<albumArt>>(emptyList()) }
        LaunchedEffect(playerFunctionality.currentFolder) {

            coroutineScope.launch {
                try {
                    albumArtList.value =
                        httpGet(trackCount = playerFunctionality.playingFolderAudioFiles.size)
                } catch (e: UnknownHostException) {
                    albumArtList.value = emptyList()
                }
            }
        }
        //
        playerFunctionality.currentFolder = playerFunctionality.musicFolder
        LazyColumn(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
            content =
            {

                playerFunctionality.playingFolderAudioFiles =
                    getAudioFiles(playerFunctionality.currentFolder)
                itemsIndexed(playerFunctionality.playingFolderAudioFiles) { index, audioFileCard ->
                    if (audioFileCard.extension in arrayOf(
                            "mp3",
                            "wav",
                            "ogg",
                            "aac",
                            "flac"
                        ) || audioFileCard.isDirectory
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = {
                                viewModelScope.launch {
                                    playerFunctionality.setPlayerQueue(
                                        player = player,
                                        playerFunctionality = playerFunctionality,
                                        index
                                    )
                                }
                            }) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                    //.border(2.dp, Color.Red))
                                )
                                {
                                    if (albumArtList.value.size >= playerFunctionality.currentFolderAudioFiles.size - 1 && albumArtList.value.isNotEmpty()) {
                                        SubcomposeAsyncImage(
                                            model = albumArtList.value.elementAt(index).download_url,
                                            loading = {
                                                CircularProgressIndicator()
                                            },
                                            contentDescription = "downloadedimage"
                                        )
                                    }
                                }
                                Text(
                                    text = audioFileCard.name,
                                    modifier = Modifier
                                        .width(300.dp)
                                        .padding(10.dp),
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                TrackDropDownMenu(
                                    playerFunctionality = playerFunctionality,
                                    player = player,
                                    audioCard = audioFileCard
                                )
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
    fun TrackDropDownMenu(
        player: ExoPlayer,
        audioCard: File,
        playerFunctionality: PlayerFunctionality
    ) {
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    Icons.Default.MoreVert,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "Open dropdown menu"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentSize()

            ) {
                //ADD TO NEXT IN QUEUE
                Button(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        playerFunctionality.setNextInQueue(player = player, audioCard = audioCard)
                    }) {
                    Text(text = "Add to next in queue")
                }
                //INTENT FEATURE
                Button(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        webSearchIntent(context, audioCard.name.dropLast(4))
                    }) {
                    Text(text = "Open Internet")
                }
            }
        }
    }
    fun webSearchIntent(context: Context, songName:String)
    {
        val encodedSearchText = Uri.encode(songName)
        val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encodedSearchText"))
        context.startActivity(openUrlIntent)
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
    private suspend fun httpGet(trackCount: Int): List<albumArt>{
        val pageNumber = Random.nextInt(from = 2, until = 20)
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .get()
                .url("https://picsum.photos/v2/list?page=$pageNumber&limit=$trackCount")
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body
            if(responseBody != null) {
                val jsonString = responseBody.string()
                val gson = Gson()
                val listType = object : TypeToken<List<albumArt>>() {}.type
                val imageList = gson.fromJson<List<albumArt>>(jsonString, listType)
                imageList
            } else {
                emptyList()
            }
        }
    }
}
