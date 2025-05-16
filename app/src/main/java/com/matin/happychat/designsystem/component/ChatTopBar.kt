package com.matin.happychat.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matin.happychat.R
import com.matin.happychat.designsystem.HappyChatIcons

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatTopBar(
    title: String = "Support",
    subtitle: String = "online",
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onInfoMenuOption: (onInfoMenuOption: InfoMenuOption) -> Unit = {},
    profileImageRes: Int = R.drawable.profile_img,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        scrollBehavior = scrollBehavior,
        title = {
            ChatTitle(title = title, subtitle = subtitle)
        },
        actions = {
            ChatActions(
                onSearchClick = onSearchClick,
                onInfoMenuOption = onInfoMenuOption
            )
        },
        navigationIcon = {
            ChatNavigationIcon(
                onBackClick = onBackClick,
                profileImageRes = profileImageRes
            )
        }
    )
}

@Composable
private fun ChatTitle(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append(title)
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append(" ")
                    append(subtitle)
                }
            },
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ChatActions(
    onSearchClick: () -> Unit,
    onInfoMenuOption: (InfoMenuOption) -> Unit
) {
    Row(
        modifier = Modifier.padding(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = HappyChatIcons.SEARCH,
                contentDescription = "search"
            )
        }
        InfoMenu(onInfoMenuOption)
    }
}

@Composable
private fun InfoMenu(onInfoMenuOption: (InfoMenuOption) -> Unit) {
    var showMenu by remember { mutableStateOf(false)}
    Box {
        IconButton(onClick = {
            showMenu = true
        }) {
            Icon(
                imageVector = HappyChatIcons.INFO,
                contentDescription = "info"
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("End session") },
                onClick = {
                    onInfoMenuOption(InfoMenuOption.END_SESSION)
                    showMenu = false
                }
            )
        }
    }
}

@Composable
private fun ChatNavigationIcon(
    onBackClick: () -> Unit,
    profileImageRes: Int
) {
    Row(
        modifier = Modifier.padding(start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = HappyChatIcons.ARROW_BACK,
                contentDescription = "navigate back"
            )
        }

        Image(
            painter = painterResource(profileImageRes),
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp),
            contentDescription = "profile image"
        )
    }
}

enum class InfoMenuOption {
    END_SESSION,
}