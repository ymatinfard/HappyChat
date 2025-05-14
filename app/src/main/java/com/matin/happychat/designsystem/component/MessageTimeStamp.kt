import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.matin.happychat.designsystem.component.TIMESTAMP_TEXT_SIZE
import com.matin.happychat.designsystem.component.chooseOnSurfaceColorFor
import java.text.SimpleDateFormat

@Composable
fun MessageTimeStamp(
    timeStamp: Long,
    isFromCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = formatTimestamp(timeStamp),
        fontSize = TIMESTAMP_TEXT_SIZE.sp,
        color = chooseOnSurfaceColorFor(isFromCurrentUser),
        modifier = modifier
    )
}

fun formatTimestamp(timeStamp: Long): String = SimpleDateFormat("HH:mm a").format(timeStamp)
