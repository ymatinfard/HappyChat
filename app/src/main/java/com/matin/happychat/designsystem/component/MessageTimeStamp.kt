import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat

@Composable
fun MessageTimeStamp(timeStamp: Long, modifier: Modifier) {
    Text(
        text = formatTime(timeStamp),
        color = MaterialTheme.colorScheme.outline,
        fontSize = 10.sp,
        modifier = modifier
            .padding(start = 4.dp)
    )
}

fun formatTime(timeStamp: Long): String = SimpleDateFormat("HH:mm a").format(timeStamp)
