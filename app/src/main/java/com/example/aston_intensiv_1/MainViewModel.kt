package com.example.aston_intensiv_1

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.example.aston_intensiv_1.data.Track
import com.example.aston_intensiv_1.data.tracks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()
    private lateinit var mediaPlayer: MediaPlayer
    private var isMediaPlayerSetupFlag = false


    fun onPlayButtonClicked(context: Context) {
        if (!uiState.value.isTrackPlaying) {
            if (!isMediaPlayerSetupFlag) {
                mediaPlayer = MediaPlayer.create(context, R.raw.akihabara)
                isMediaPlayerSetupFlag = true
            }
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()

        }
        _uiState.update {
            it.copy(
                isTrackPlaying = !it.isTrackPlaying
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}

data class MainUiState(
    val isTrackPlaying: Boolean = false,
    val selectedTrack: Track = tracks.first()
)