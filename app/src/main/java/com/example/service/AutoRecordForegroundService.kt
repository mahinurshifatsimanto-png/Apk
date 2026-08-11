package com.example.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.utils.AudioRecorderManager

class AutoRecordForegroundService : Service() {

    private lateinit var audioRecorderManager: AudioRecorderManager
    private val CHANNEL_ID = "AutoRecordChannel"
    private val NOTIF_ID = 1001

    override fun onCreate() {
        super.onCreate()
        audioRecorderManager = AudioRecorderManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val subject = intent?.getStringExtra("SUBJECT") ?: "College Lecture"

        when (action) {
            ACTION_START_RECORDING -> {
                audioRecorderManager.startRecording(subject)
                startForeground(NOTIF_ID, createNotification("Auto-Recording Active: $subject"))
            }
            ACTION_STOP_RECORDING -> {
                audioRecorderManager.stopRecording()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                startForeground(NOTIF_ID, createNotification("Routine Auto-Record Scheduler Active"))
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Auto-Record Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifies when background lecture auto-recording is active"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MY COLLEGE NOTES")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_START_RECORDING = "com.example.service.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.example.service.STOP_RECORDING"

        fun startAutoRecord(context: Context, subject: String) {
            val intent = Intent(context, AutoRecordForegroundService::class.java).apply {
                action = ACTION_START_RECORDING
                putExtra("SUBJECT", subject)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopAutoRecord(context: Context) {
            val intent = Intent(context, AutoRecordForegroundService::class.java).apply {
                action = ACTION_STOP_RECORDING
            }
            context.startService(intent)
        }
    }
}
