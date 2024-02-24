package com.example.aston_intensiv_1.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.aston_intensiv_1.musicservices.MusicService
import com.example.aston_intensiv_1.musicservices.MusicService.Actions
import com.example.aston_intensiv_1.data.Track
import com.example.aston_intensiv_1.data.tracks
import com.example.aston_intensiv_1.nextTrack
import com.example.aston_intensiv_1.previousTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()
    private var isMusicServiceSetup: Boolean = false


    fun onPlayButtonClicked(context: Context) {
        if (!isMusicServiceSetup) {
            Intent(context, MusicService::class.java).also { intent ->
                intent.action = Actions.START_SERVICE.name
                context.startService(intent)
            }
            isMusicServiceSetup = true
        }

        val intent = Intent(context, MusicService::class.java)
        intent.action = Actions.PLAY_TOGGLE.name
        context.startService(intent)

        _uiState.update {
            it.copy(
                isTrackPlaying = !it.isTrackPlaying
            )
        }
    }

    fun setPrevTrack(context: Context) {
        val prevPosition = uiState.value.selectedTrack.position.previousTrack()
        changeTrack(context, prevPosition)
    }

    fun setNextTrack(context: Context) {
        val nextPosition = uiState.value.selectedTrack.position.nextTrack()
        changeTrack(context, nextPosition)
    }

    private fun changeTrack(context: Context, position: Int) {
        _uiState.update {
            it.copy(
                selectedTrack = tracks[position]
            )
        }
        val intent = Intent(context, MusicService::class.java)
        intent.action = Actions.CHANGE_TRACK.name
        intent.putExtra("TRACK_POSITION", position)
        context.startService(intent)


    }

    fun stopService(context: Context) {
        val intent = Intent(context, MusicService::class.java)
        context.stopService(intent)
    }
}

data class MainUiState(
    val isTrackPlaying: Boolean = false,
    val selectedTrack: Track = tracks.first()
)