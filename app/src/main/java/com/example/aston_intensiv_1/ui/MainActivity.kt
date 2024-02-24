package com.example.aston_intensiv_1.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aston_intensiv_1.R
import com.example.aston_intensiv_1.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    updateUi(uiState)
                }
            }
        }
        setListeners()
        checkNotificationPermission()
    }

    private fun setListeners() {
        binding.apply {
            play.setOnClickListener {
                viewModel.onPlayButtonClicked(this@MainActivity)
            }
            prev.setOnClickListener {
                viewModel.setPrevTrack(this@MainActivity)
            }
            next.setOnClickListener {
                viewModel.setNextTrack(this@MainActivity)
            }
        }
    }

    private fun updateUi(state: MainUiState) {
        val track = state.selectedTrack
        val playButtonImgResource =
            if (state.isTrackPlaying) R.drawable.ic_pause else R.drawable.ic_play
        binding.trackImage.setImageResource(track.imgResourceId)
        binding.title.text = track.title
        binding.play.setImageResource(playButtonImgResource)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopService(this)
    }
}
