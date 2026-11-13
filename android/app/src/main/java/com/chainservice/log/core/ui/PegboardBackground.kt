package com.chainservice.log.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chainservice.log.R
import com.chainservice.log.core.theme.PlateElevated

@Composable
fun PegboardBackground(
    overlay: List<Color>,
    blobColor: Color,
    blobAlignment: Alignment,
    modifier: Modifier = Modifier,
    dotTint: Color = PlateElevated,
    dotAlpha: Float = 0.5f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 22.dp.toPx()
            val radius = 1.5.dp.toPx()
            var y = spacing / 2f
            while (y < size.height) {
                var x = spacing / 2f
                while (x < size.width) {
                    drawCircle(
                        color = dotTint,
                        radius = radius,
                        center = Offset(x, y),
                        alpha = dotAlpha
                    )
                    x += spacing
                }
                y += spacing
            }
        }
        Box(
            modifier = Modifier
                .align(blobAlignment)
                .size(320.dp)
                .background(
                    Brush.radialGradient(
                        listOf(blobColor.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )
        content()
    }
}
