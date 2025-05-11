package com.matin.happychat

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.matin.happychat.chat.ChatScreen
import com.matin.happychat.designsystem.theme.HappyChatTheme
import com.matin.happychat.mediaplayer.VoiceMessagePlayer
import com.matin.happychat.mediaplayer.VoiceMessageRecorder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var voiceMessagePlayer: VoiceMessagePlayer

    @Inject
    lateinit var voiceMessageRecorder: VoiceMessageRecorder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            HappyChatTheme {
                ChatScreen(
                    voiceMessagePlayer = voiceMessagePlayer,
                    voiceMessageRecorder = voiceMessageRecorder,
                    onNavigateBack = { }
                )
            }
        }
    }
}


