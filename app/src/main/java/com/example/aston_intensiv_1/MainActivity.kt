package com.example.aston_intensiv_1

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopService(this)
    }
}
