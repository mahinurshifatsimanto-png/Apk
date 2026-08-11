package com.example.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorderManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _recordingDurationSeconds = MutableStateFlow(0L)
    val recordingDurationSeconds: StateFlow<Long> = _recordingDurationSeconds

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude

    private var timerTask: Timer? = null

    fun startRecording(subjectName: String): File? {
        stopRecording()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = File(context.filesDir, "MyCollegeNotes/Recordings")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "REC_${subjectName.replace(" ", "_")}_$timeStamp.m4a")
        currentOutputFile = file

        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            _isRecording.value = true
            _isPaused.value = false
            _recordingDurationSeconds.value = 0L

            startTimer()
            return file
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error starting recording: ${e.message}")
            stopRecording()
            return null
        }
    }

    fun pauseRecording() {
        if (_isRecording.value && !_isPaused.value) {
            try {
                mediaRecorder?.pause()
                _isPaused.value = true
            } catch (e: Exception) {
                Log.e("AudioRecorderManager", "Error pausing: ${e.message}")
            }
        }
    }

    fun resumeRecording() {
        if (_isRecording.value && _isPaused.value) {
            try {
                mediaRecorder?.resume()
                _isPaused.value = false
            } catch (e: Exception) {
                Log.e("AudioRecorderManager", "Error resuming: ${e.message}")
            }
        }
    }

    fun stopRecording(): File? {
        timerTask?.cancel()
        timerTask = null

        if (_isRecording.value) {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
            } catch (e: Exception) {
                Log.e("AudioRecorderManager", "Error stopping: ${e.message}")
            } finally {
                mediaRecorder = null
                _isRecording.value = false
                _isPaused.value = false
            }
        }
        return currentOutputFile
    }

    private fun startTimer() {
        timerTask?.cancel()
        timerTask = Timer()
        timerTask?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (_isRecording.value && !_isPaused.value) {
                    _recordingDurationSeconds.value += 1
                    try {
                        val maxAmp = mediaRecorder?.maxAmplitude ?: 0
                        _amplitude.value = maxAmp
                    } catch (e: Exception) {
                        _amplitude.value = (1000..8000).random()
                    }
                }
            }
        }, 1000, 1000)
    }

    fun getFormattedFileSize(): String {
        val file = currentOutputFile ?: return "0 KB"
        val bytes = file.length()
        return when {
            bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format(Locale.US, "%d KB", bytes / 1024)
            else -> "$bytes B"
        }
    }
}
