package com.matin.happychat.designsystem.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun MediaPickerLauncher(
    showPicker: Boolean,
    onMediaSelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onMediaSelected(it) }
        onDismiss()
    }

    LaunchedEffect(showPicker) {
        if (showPicker) {
            launcher.launch("image/*")
        }
    }
}