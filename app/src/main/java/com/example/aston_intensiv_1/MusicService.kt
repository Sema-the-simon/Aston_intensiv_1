package com.example.aston_intensiv_1

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import com.example.aston_intensiv_1.MusicService.Actions.*

class MusicService : Service() {

    private lateinit var player: MediaPlayer
    private var currentSong: Int = -1

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val action: String = intent.action.toString()
        when(action) {
            START_SERVICE.name -> onStartService()
            STOP_SERVICE.name -> onStopService()
            LOAD.name -> onLoad(intent)
            PLAY.name -> onPlay(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onLoad(intent: Intent) {
        val songId = intent.extras?.getInt(SONG_ID)
        if (currentSong != songId && songId != null) {
            currentSong = songId
            loadNewSong(songId)
        }

        val songNameNullable = intent.extras?.getString(SONG_NAME)
        val artistNameNullable = intent.extras?.getString(ARTIST_NAME)
        let2(songNameNullable, artistNameNullable) { songName, artistName ->
            updateNotification(songName, artistName)
        }
    }

    private fun onPlay(intent: Intent) {
        val isPlaying = intent.extras?.getBoolean(IS_PLAYING)
        isPlaying?.let {
            if (it) {
                player.start()
            } else {
                player.pause()
            }
        }
    }

    private fun loadNewSong(songId: Int) {
        player.stop()
        player.reset()
        player = MediaPlayer.create(this, songId)
    }

    private fun onStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                MUSIC_NOTIFICATION_ID,
                getNotification("NAME", "ARTIST"),
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(
                MUSIC_NOTIFICATION_ID,
                getNotification("NAME", "ARTIST"),
            )
        }
        if (!this::player.isInitialized) {
            player = MediaPlayer.create(this, R.raw.zabej_lerochka)
        }
    }

    private fun onStopService() {
        player.release()
        stopSelf()
    }

    private fun getNotification(songName: String, artistName: String) =
        NotificationCompat.Builder(this, MUSIC_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentTitle(songName)
            .setContentText(artistName)
            .build()

    private fun updateNotification(songName: String, artistName: String) {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(MUSIC_NOTIFICATION_ID, getNotification(songName, artistName))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    enum class Actions {
        START_SERVICE,
        STOP_SERVICE,
        LOAD,
        PLAY
    }
}