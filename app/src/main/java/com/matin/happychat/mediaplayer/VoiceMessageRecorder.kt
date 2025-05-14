package com.matin.happychat.mediaplayer

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

interface AudioRecorder {
    fun startRecording()
    fun stopRecording(): String?
    fun release()
}

interface AudioFileStorage {
    fun createOutputFile(): File
}

data class AudioRecorderConfig(
    val audioSource: Int = MediaRecorder.AudioSource.MIC,
    val outputFormat: Int = MediaRecorder.OutputFormat.THREE_GPP,
    val audioEncoder: Int = MediaRecorder.AudioEncoder.AMR_NB,
)

@Singleton
class ExternalAudioFileStorage @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioFileStorage {

    private val dateFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    override fun createOutputFile(): File {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            ?: throw IOException("Failed to access external storage directory")

        val fileName = "audio_${generateUniqueFileName()}.3gp"
        return File(outputDir, fileName)
    }

    private fun generateUniqueFileName(): String = dateFormatter.format(Date())
}

@Singleton
class VoiceMessageRecorder @Inject constructor(
    private val fileStorage: AudioFileStorage,
) : AudioRecorder {

    private val config = AudioRecorderConfig()
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null

    @Throws(IOException::class)
    override fun startRecording() {
        release() // Ensure any previous recorder is released

        currentOutputFile = fileStorage.createOutputFile()

        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(config.audioSource)
                setOutputFormat(config.outputFormat)
                setAudioEncoder(config.audioEncoder)
                setOutputFile(currentOutputFile?.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            release()
            throw IOException("Failed to start recording: ${e.message}", e)
        }
    }

    override fun stopRecording(): String? {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }

            return currentOutputFile?.absolutePath
        } catch (e: Exception) {
            Log.e("VoiceMessageRecorder", "Error stopping recording", e)
            return null
        } finally {
            mediaRecorder = null
        }
    }

    override fun release() {
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            Log.e("VoiceMessageRecorder", "Error releasing media recorder", e)
        } finally {
            mediaRecorder = null
        }
    }
}
