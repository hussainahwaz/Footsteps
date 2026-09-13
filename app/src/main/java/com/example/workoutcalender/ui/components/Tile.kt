package com.example.workoutcalender.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workoutcalender.ui.theme.BodyFont
import com.example.workoutcalender.ui.theme.LocalConsistencyColors
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape

/**
 * A single day-tile in the contribution grid.
 *
 * Handles its own "just completed" pop animation: when [filled] flips from
 * false -> true it briefly scales up then settles, rather than snapping.
 * Supports plain tap, and an optional long-press (used by "Both" completion
 * trackers to open the detailed entry sheet).
 */
@Composable
fun Tile(
    filled: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    cornerRadius: Dp = 9.dp,
    isToday: Boolean = false,
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
) {
    val colors = LocalConsistencyColors.current
    var justFilled by remember { mutableStateOf(false) }
    var isFirstComposition by remember { mutableStateOf(true) }

    androidx.compose.runtime.LaunchedEffect(filled) {
        if (filled && !isFirstComposition) {
            justFilled = true
            delay(180)
            justFilled = false
        }
        isFirstComposition = false
    }

    val scale by animateFloatAsState(
        targetValue = if (justFilled) 1.08f else 1f,
        animationSpec = tween(180),
        label = "tileScale",
    )

    val shape = if (isToday) CircleShape else RoundedCornerShape(cornerRadius)
    // Ring only needs to contrast with the background behind it (the gap),
    // not with the fill -- so keep it constant regardless of `filled`.
    val ringColor = colors.textDim

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .then(
                if (isToday) Modifier.border(2.dp, ringColor, shape) else Modifier
            )
            .padding(if (isToday) 3.dp else 0.dp)
            .clip(shape)
            .background(if (filled) color else colors.tileEmpty)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = onTap != null || onLongPress != null,
                onClick = { onTap?.invoke() },
                onLongClick = { onLongPress?.invoke() },
            ),
    )
}

@Composable
fun NumberTile(
    day: Int,
    filled: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    isToday: Boolean = false,
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
) {
    val colors = LocalConsistencyColors.current
    val shape = if (isToday) CircleShape else RoundedCornerShape(9.dp)
    val ringColor = colors.textDim

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (isToday) Modifier.border(1.5.dp, ringColor, shape) else Modifier
            )
            .padding(if (isToday) 3.dp else 0.dp)
            .clip(shape)
            .background(if (filled) color else colors.tileEmpty)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = onTap != null || onLongPress != null,
                onClick = { onTap?.invoke() },
                onLongClick = { onLongPress?.invoke() },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            fontFamily = BodyFont,
            fontSize = 13.sp,
            color = if (filled) Color.White else colors.textDim,
            textAlign = TextAlign.Center,
        )
    }
}
