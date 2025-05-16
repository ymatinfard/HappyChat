package com.matin.happychat.designsystem.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.matin.happychat.designsystem.theme.HappyChatTheme

@Composable
fun LoadingPulse(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 8.dp,
    travelDistance: Dp = 20.dp,
) {
    val transition = rememberInfiniteTransition(label = "loading_circles")
    val delays = listOf(0, 100, 200) //ms

    val animatedOffsets = delays.map { delay ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.0f at 0 with LinearOutSlowInEasing
                    1.0f at 300 with LinearOutSlowInEasing
                    0.0f at 600 with LinearOutSlowInEasing
                    0.0f at 1200 with LinearOutSlowInEasing
                },
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(delay)
            ),
            label = "circle_animation"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween)
    ) {
        animatedOffsets.forEach { offset ->
            Box(
                modifier = Modifier
                    .size(size)
                    .graphicsLayer {
                        translationY = -offset.value * travelDistance.toPx()
                    }
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}

@Preview
@Composable
fun LoadingCirclePreview() {
    HappyChatTheme {
        LoadingPulse(
            modifier = Modifier,
            size = 20.dp,
            color = MaterialTheme.colorScheme.primary,
            spaceBetween = 8.dp,
            travelDistance = 20.dp,
        )
    }
}