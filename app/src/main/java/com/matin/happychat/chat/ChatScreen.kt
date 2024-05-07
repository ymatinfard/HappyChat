package com.matin.happychat.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    TODO("Not yet implemented")
}

@Composable
fun MessageList() {
    TODO("Not yet implemented")
}
