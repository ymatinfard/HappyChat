package com.matin.happychat.mediaplayer

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HappyChatMediaRecorder @Inject constructor(@ApplicationContext private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    var outputFile: File? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun startRecording() {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        outputFile = generateFileIn(outputDir)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile)
            prepare()
            start()
        }
    }

    fun stopRecording(onSendVoiceMessage: (String) -> Unit) {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        outputFile?.let { file ->
            onSendVoiceMessage(file.path)
        }
    }

    private fun generateFileIn(outputDir: File?) =
        File(outputDir, "audio_${generateUniqueFileName()}.3gp")

}

fun generateUniqueFileName(): String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

