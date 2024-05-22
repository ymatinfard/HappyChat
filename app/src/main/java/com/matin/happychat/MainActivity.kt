package com.matin.happychat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyChatTheme {
                val viewModel = viewModel<ChatViewModel>()
                ChatScreen(viewModel = viewModel, player = happyChatMediaPlayer, mediaRecorder = mediaRecorder)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HappyChatTheme {
        Greeting("Android")
    }
}