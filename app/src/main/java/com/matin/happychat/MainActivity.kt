package com.matin.happychat

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.matin.happychat.chat.ChatScreen
import com.matin.happychat.chat.ChatViewModel
import com.matin.happychat.designsystem.theme.HappyChatTheme
import com.matin.happychat.mediaplayer.HappyChatMediaPlayer
import com.matin.happychat.mediaplayer.HappyChatMediaRecorder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var happyChatMediaPlayer: HappyChatMediaPlayer

    @Inject
    lateinit var mediaRecorder: HappyChatMediaRecorder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            HappyChatTheme {
                val viewModel = viewModel<ChatViewModel>()
                ChatScreen(
                    viewModel = viewModel,
                    player = happyChatMediaPlayer,
                    mediaRecorder = mediaRecorder
                )
            }
        }
    }
}

