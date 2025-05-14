package com.matin.happychat.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun chooseOnSurfaceColorFor(isFromCurrentUser: Boolean) =
    if (isFromCurrentUser)
        MaterialTheme.colorScheme.onTertiary
    else
        MaterialTheme.colorScheme.onPrimary