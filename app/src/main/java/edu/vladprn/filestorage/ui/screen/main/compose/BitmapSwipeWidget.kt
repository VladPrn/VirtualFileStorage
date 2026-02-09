package edu.vladprn.filestorage.ui.screen.main.compose

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

@Composable
fun BitmapSwipeWidget(
    modifier: Modifier,
    bitmap: Bitmap,
    onLeftSwipe: () -> Unit,
    onRightSwipe: () -> Unit,
) {
    val swipeThreshold = with(LocalDensity.current) { 100.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                val drawOffset = mutableFloatStateOf(0f)

                detectHorizontalDragGestures(
                    onDragStart = { drawOffset.floatValue = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        drawOffset.floatValue += dragAmount
                        change.consume()
                    },
                    onDragEnd = {
                        when {
                            drawOffset.floatValue > swipeThreshold -> onRightSwipe()
                            drawOffset.floatValue < -swipeThreshold -> onLeftSwipe()
                        }
                        drawOffset.floatValue = 0f
                    }
                )
            }
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null
        )
    }

    val context = LocalContext.current
    val window = (context as? Activity)?.window
    val wic = window?.let { WindowCompat.getInsetsController(window, window.decorView) }

    DisposableEffect(Unit) {
        wic?.hide(WindowInsetsCompat.Type.statusBars())

        onDispose {
            wic?.show(WindowInsetsCompat.Type.statusBars())
        }
    }
}
