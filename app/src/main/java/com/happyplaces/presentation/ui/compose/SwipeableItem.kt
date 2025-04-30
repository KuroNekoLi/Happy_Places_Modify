package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import kotlin.math.roundToInt

private enum class SwipeAction { Idle, Delete, Edit }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableItem(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val swipeState = remember { AnchoredDraggableState<SwipeAction>(SwipeAction.Idle) }
    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }
    var anchors by remember(widthPx) {
        mutableStateOf(
            DraggableAnchors<SwipeAction> {
                SwipeAction.Delete at -widthPx.toFloat()
                SwipeAction.Idle at 0f
                SwipeAction.Edit at widthPx.toFloat()
            }
        )
    }

    LaunchedEffect(anchors) {
        swipeState.updateAnchors(anchors)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .onSizeChanged { size ->
                widthPx = size.width
                heightPx = size.height
                anchors = DraggableAnchors {
                    SwipeAction.Delete at -widthPx.toFloat()
                    SwipeAction.Idle at 0f
                    SwipeAction.Edit at widthPx.toFloat()
                }
                swipeState.updateAnchors(anchors)
            }
            .background(Color.Gray)
    ) {
        val offsetPx = swipeState.offset.takeIf { it.isFinite() } ?: 0f
        val density = LocalDensity.current
        val iconSizeDp = 24.dp
        val widthDp = with(density) { widthPx.toDp() }
        val heightDp = with(density) { heightPx.toDp() }
        val iconPaddingHorizontalDp = (widthDp * 0.2f - iconSizeDp) / 2f

        // 背景區域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    when {
                        offsetPx > 0f -> Color(0xFF24AE05)
                        offsetPx < 0f -> Color(0xFFF44336)
                        else -> Color.Transparent
                    }
                )
                .align(if (offsetPx > 0f) Alignment.CenterStart else Alignment.CenterEnd)
                .height(heightDp)
        ) {
            val icon = if (offsetPx > 0f) Icons.Default.Edit else Icons.Default.Delete
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(if (offsetPx > 0f) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(
                        start = if (offsetPx > 0f) iconPaddingHorizontalDp else 0.dp,
                        end = if (offsetPx < 0f) iconPaddingHorizontalDp else 0.dp
                    )
            )
        }

        // 前景內容 + 拖曳
        Box(
            Modifier
                .offset { IntOffset(offsetPx.roundToInt(), 0) }
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .anchoredDraggable(swipeState, orientation = Orientation.Horizontal)
        ) {
            content()
        }
    }

    // 放手後決定行為
    LaunchedEffect(swipeState.currentValue) {
        when (swipeState.currentValue) {
            SwipeAction.Edit -> {
                onEdit()
                swipeState.snapTo(SwipeAction.Idle)
            }

            SwipeAction.Delete -> {
                onDelete()
                swipeState.snapTo(SwipeAction.Idle)
            }

            SwipeAction.Idle -> {
                swipeState.snapTo(SwipeAction.Idle)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SwipeableItemPreview() {
    HappyPlacesTheme {
        SwipeableItem(
            modifier = Modifier.fillMaxWidth(),
            onDelete = {},
            onEdit = {}
        ) {
            Text(
                text = "SwipeableItem",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}