package com.matin.happychat.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matin.happychat.common.HappyChatIcons

@Preview
@Composable
fun ChatScreen() {
    Scaffold(
        topBar = {
            ChatTopBar()
        }
    ) {
        Column(Modifier.padding(it)) {
            // MessageList()
            //  MessageInput()
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChatTopBar() {
    CenterAlignedTopAppBar(modifier =
    Modifier.padding(start = 8.dp, end = 8.dp),
        title = {
            Column {
                Text("Help center", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }, actions = {
            Row {
                Icon(imageVector = HappyChatIcons.SEARCH, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Icon(imageVector = HappyChatIcons.INFO, contentDescription = null)
            }
        },
        navigationIcon = {
            Icon(
                imageVector = HappyChatIcons.HAPPY_CHAT,
                contentDescription = null
            )
        })
}

@Composable
fun MessageInput() {
    TODO("Not yet implemented")
}

@Composable
fun MessageList() {
    TODO("Not yet implemented")
}
