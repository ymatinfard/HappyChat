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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matin.happychat.R
import com.matin.happychat.designsystem.HappyChatIcons
import com.matin.happychat.designsystem.theme.HappyChatTheme

@Preview(showBackground = true)
@Composable
fun ChatScreen() {
    HappyChatTheme {
        Scaffold(
            topBar = {
                ChatTopBar()
            }
        ) {
            Column(Modifier.padding(it)) {
                // MessageList()
                MessageInput()
            }
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
                Text(buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(stringResource(R.string.happy))
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 12.sp,
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
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = HappyChatIcons.INFO,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },
        navigationIcon = {
            Icon(
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified,
                painter = painterResource(id = R.drawable.ic_happy_chat),
                contentDescription = null
            )
        })
}

@Composable
fun MessageInput() {
    var message by remember {
        mutableStateOf("")
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(start = 8.dp, end = 8.dp),
        ) {

            TextField(
                value = message,
                onValueChange = {
                    message = it
                },
                label = {
                    Text(text = "Enter a message")
                },
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Unspecified),
                modifier = Modifier.weight(1f)

            )

            Button(
                onClick = {

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
fun MessageList() {
    Box(modifier = Modifier.fillMaxSize())
}

const val DISABLED_ALPHA = 0.38F
