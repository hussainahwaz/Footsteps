package com.example.workoutcalender.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import com.example.workoutcalender.model.Tracker

/**
 * A plain Column (tracker lists are short, so virtualization isn't needed) that lets
 * any row be dragged up/down by its handle to reorder [trackers]. [onMove] is called
 * live as the dragged row crosses a neighbor's midpoint -- the caller reorders (and
 * persists) its backing list in response; this component holds no state of its own
 * beyond the drag gesture itself.
 *
 * [itemContent] renders one row; it's handed a Modifier that must be attached to
 * whatever should act as the drag handle (e.g. a small grip icon), not the whole row --
 * the row still needs its own tap/long-press behavior to work independently of dragging.
 */
@Composable
fun ReorderableTrackerList(
    trackers: List<Tracker>,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (index: Int, tracker: Tracker, dragHandleModifier: Modifier) -> Unit,
) {
    // Measured height (px) of each row, keyed by tracker id so it survives reordering.
    val heights = remember { mutableStateMapOf<String, Int>() }
    var draggedId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    // The drag gesture coroutine below is long-running and keyed on tracker.id (so it
    // isn't torn down mid-drag when the list reorders) -- rememberUpdatedState makes
    // sure it always reads the *current* list rather than the one captured when the
    // gesture started.
    val currentTrackers by rememberUpdatedState(trackers)

    Column(modifier = modifier) {
        trackers.forEachIndexed { index, tracker ->
            key(tracker.id) {
                val isDragged = tracker.id == draggedId
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coords -> heights[tracker.id] = coords.size.height }
                        .zIndex(if (isDragged) 1f else 0f)
                        .graphicsLayer {
                            translationY = if (isDragged) dragOffset else 0f
                        },
                ) {
                    val handleModifier = Modifier.pointerInput(tracker.id) {
                        detectDragGestures(
                            onDragStart = {
                                draggedId = tracker.id
                                dragOffset = 0f
                            },
                            onDragEnd = {
                                draggedId = null
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                draggedId = null
                                dragOffset = 0f
                            },
                            onDrag = { change, amount ->
                                change.consume()
                                dragOffset += amount.y

                                val list = currentTrackers
                                val currentIndex = list.indexOfFirst { it.id == draggedId }
                                val rowHeight = heights[tracker.id]
                                if (currentIndex == -1 || rowHeight == null) return@detectDragGestures

                                if (dragOffset > rowHeight / 2 && currentIndex < list.lastIndex) {
                                    onMove(currentIndex, currentIndex + 1)
                                    dragOffset -= rowHeight
                                } else if (dragOffset < -rowHeight / 2 && currentIndex > 0) {
                                    onMove(currentIndex, currentIndex - 1)
                                    dragOffset += rowHeight
                                }
                            },
                        )
                    }
                    itemContent(index, tracker, handleModifier)
                }
            }
        }
    }
}