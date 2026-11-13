package com.chainservice.log.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.R
import com.chainservice.log.core.config.GameConfig
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.theme.CharcoalDeep
import com.chainservice.log.core.theme.Lime
import com.chainservice.log.core.theme.PlateBorder
import com.chainservice.log.core.theme.PlateHighlight
import com.chainservice.log.core.theme.PlateSurface
import com.chainservice.log.core.theme.PlateTrack
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.PegboardBackground

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) {
            onFinished()
        }
    }

    val entry = remember { Animatable(0f) }
    LaunchedEffect(splashViewModel) {
        entry.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
    }

    val plateScale = 0.86f + 0.14f * entry.value
    val plateAlpha = entry.value
    val titleOffset = 24f * (1f - entry.value)

    val transition = rememberInfiniteTransition(label = "splashLoops")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.SPLASH_SWEEP_MS, easing = LinearEasing)
        ),
        label = "chainSweep"
    )
    val wrenchAngle by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.SPLASH_WRENCH_MS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wrenchTurn"
    )
    val glow by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.SPLASH_GLOW_MS),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleGlow"
    )

    PegboardBackground(
        overlay = listOf(
            Color(0xE61A1D21),
            Color(0xF21A1D21),
            CharcoalDeep
        ),
        blobColor = SignalOrange,
        blobAlignment = Alignment.TopStart,
        dotAlpha = 0.55f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(196.dp)
                    .graphicsLayer {
                        scaleX = plateScale
                        scaleY = plateScale
                        alpha = plateAlpha
                    }
                    .background(PlateSurface, RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, PlateBorder), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    PlateHighlight.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    modifier = Modifier.size(148.dp),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .size(44.dp)
                        .graphicsLayer { rotationZ = wrenchAngle },
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.width(240.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(12) { index ->
                    val lit = (sweep.toInt() % 12) == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .background(
                                color = if (lit) Lime else PlateTrack,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Box(
                modifier = Modifier.graphicsLayer { translationY = titleOffset },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 260.dp, height = 90.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    SignalOrange.copy(alpha = glow * 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.splash_title_top),
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.splash_title_bottom),
                        style = MaterialTheme.typography.displaySmall,
                        color = SignalOrange,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp),
                color = SignalOrange,
                trackColor = PlateTrack
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.splash_progress),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}
