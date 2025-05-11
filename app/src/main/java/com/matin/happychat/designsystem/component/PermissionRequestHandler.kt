package com.matin.happychat.designsystem.component

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

@Composable
fun PermissionRequestHandler(
    permissionsToRequest: Set<String>,
    onPermissionResult: (Map<String, Boolean>) -> Unit,
    onShowRationaleText: (String) -> String = { "We need access to $it for this feature to work properly." },
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var rationaleQueue by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentRationale by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            onPermissionResult(result)
        }
    )

    LaunchedEffect(permissionsToRequest) {
        if (permissionsToRequest.isNotEmpty()) {
            val rationales = permissionsToRequest.filter { permission ->
                activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
                } == true
            }

            if (rationales.isNotEmpty()) {
                rationaleQueue = rationales
                currentRationale = rationales.first()
            } else {
                launcher.launch(permissionsToRequest.toTypedArray())
            }
        }
    }

    // Show rationale dialog if needed
    currentRationale?.let { permission ->
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Permission Required") },
            text = { Text(onShowRationaleText(permission)) },
            confirmButton = {
                TextButton(onClick = {
                    rationaleQueue = rationaleQueue.drop(1)
                    currentRationale = rationaleQueue.firstOrNull()
                    if (currentRationale == null) {
                        // All rationales shown, launch permissions
                        launcher.launch(permissionsToRequest.toTypedArray())
                    }
                }) {
                    Text("Continue")
                }
            }
        )
    }
}

