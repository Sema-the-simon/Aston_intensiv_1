package com.example.aston_intensiv_1

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.aston_intensiv_1.R
import com.example.aston_intensiv_1.data.tracks
import com.example.aston_intensiv_1.musicservices.MusicService.Actions.CHANGE_TRACK
import com.example.aston_intensiv_1.musicservices.MusicService.Actions.PLAY_TOGGLE
import com.example.aston_intensiv_1.musicservices.MusicService.Actions.START_SERVICE
import com.example.aston_intensiv_1.musicservices.MusicService.Actions.STOP_SERVICE
import com.example.aston_intensiv_1.nextTrack
import com.example.aston_intensiv_1.previousTrack
import com.example.aston_intensiv_1.ui.MainActivity

const val MUSIC_SERVICE_ID = 10
const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "Deadline"

class MusicService : Service() {
    private var player: MediaPlayer? = null
    private var trackPosition: Int = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val action: String = intent.action.toString()
        when (action) {
            START_SERVICE.name -> onStartService()
            PLAY_TOGGLE.name -> onPlayToggle()
            CHANGE_TRACK.name -> changeTrack(intent.getIntExtra("TRACK_POSITION", 0))
            STOP_SERVICE.name -> onStopService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onStartService() {
        updateNotification(trackPosition)
    }

    private fun onPlayToggle() {
        if (player == null) {
            player = MediaPlayer.create(this, tracks[0].musicResourceId)
            player?.isLooping = true
        }
        if (player!!.isPlaying) {
            player?.pause()
        } else {
            player?.start()
        }
        updateNotification(trackPosition)
    }

    private fun changeTrack(position: Int) {
        var isContinuePlaying = false
        if (player != null) {
            isContinuePlaying = player!!.isPlaying
            player?.stop()
        }
        player = MediaPlayer.create(this, tracks[position].musicResourceId)
        if (isContinuePlaying)
            player?.start()

        trackPosition = position
        updateNotification(position)
    }

    private fun onStopService() {
        player?.release()
        player = null
    }

    private fun getNotification(position: Int): Notification {
        val track = tracks[position]
        val trackImage = BitmapFactory.decodeResource(resources, track.imgResourceId)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(trackImage)
            .setContentTitle(track.title)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification(trackPosition: Int) {
        val notification = getNotification(trackPosition)
        ServiceCompat.startForeground(
            this,
            MUSIC_SERVICE_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            }
        )
    }


    enum class Actions {
        START_SERVICE,
        STOP_SERVICE,
        PLAY_TOGGLE,
        CHANGE_TRACK
    }
}