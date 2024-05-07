package com.matin.happychat.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matin.happychat.R
import com.matin.happychat.designsystem.HappyChatIcons
import kotlinx.coroutines.flow.map

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar()
        }
    ) { innerPadding ->
        val messages by viewModel.uiState.map { it.messages }
            .collectAsState(initial = emptyList())

        val currentMessage by viewModel.uiState.map { it.currentMessage }
            .collectAsState(initial = "")

        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MessageList(messages)
            MessageInput(currentMessage, viewModel::onUpdateMessage, viewModel::onSendMessage)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChatTopBar() {
    CenterAlignedTopAppBar(modifier =
    Modifier
        .padding(start = 8.dp, end = 8.dp),
        title = {
            Column {
                Text(buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(stringResource(R.string.happy))
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.DarkGray
                        )
                    ) {
                        append(" ")
                        append(stringResource(R.string.online))
                    }
                })
            }
        }, actions = {
            Row {
                Icon(
                    imageVector = HappyChatIcons.SEARCH,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = HappyChatIcons.INFO,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        navigationIcon = {
            Icon(
                modifier = Modifier.size(32.dp),
                tint = Color.Unspecified,
                painter = painterResource(id = R.drawable.ic_happy_chat),
                contentDescription = null
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    message: String,
    onUpdateMessage: (String) -> Unit,
    onSendMessageClick: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(start = 8.dp, end = 8.dp)
        ) {

            TextField(
                value = message,
                onValueChange = {
                    onUpdateMessage(it)
                },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
            )

            Button(
                onClick = {
                    onSendMessageClick(message)
                },
                border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.onPrimary),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContentColor = LocalContentColor.current.copy(alpha = DISABLED_ALPHA)
                )
            ) {
                Text(text = "Send")
            }

        }
    }
}

@Composable
fun MessageList(messages: List<Message>) {
    Box {
        LazyColumn {
            items(messages, key = {
                it.timeStamp
            }) {
                Text(text = it.message)
            }
        }
    }
}

const val DISABLED_ALPHA = 0.38F
