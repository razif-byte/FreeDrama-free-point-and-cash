package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.DramaEntity
import com.example.data.model.EpisodeEntity
import com.example.ui.theme.CoinGold
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangeVibrant
import kotlinx.coroutines.delay

@Composable
fun CinematicDramaCanvas(
    drama: DramaEntity,
    episode: EpisodeEntity,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    onDoubleTapLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var heartVisible by remember { mutableStateOf(false) }
    var tapPosition by remember { mutableStateOf(Offset.Zero) }

    // Progress simulation
    var progress by remember { mutableFloatStateOf(0.15f) }
    LaunchedEffect(isPlaying, episode.id) {
        progress = 0.05f
        while (isPlaying) {
            delay(1000)
            if (progress < 0.98f) {
                progress += (1f / episode.durationSeconds.toFloat())
            } else {
                progress = 0.05f
            }
        }
    }

    // Infinite animation for cinematic ambient motion
    val infiniteTransition = rememberInfiniteTransition(label = "drama_canvas")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    val lightSweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    // Poster resource resolution
    val imageResId = remember(drama.posterResName) {
        val res = context.resources.getIdentifier(
            drama.posterResName,
            "drawable",
            context.packageName
        )
        if (res != 0) res else null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .pointerInput(episode.id) {
                detectTapGestures(
                    onTap = {
                        onTogglePlayPause()
                    },
                    onDoubleTap = { offset ->
                        tapPosition = offset
                        heartVisible = true
                        onDoubleTapLike()
                    }
                )
            }
    ) {
        // Base Poster / Cinematic Visual
        if (imageResId != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageResId)
                    .crossfade(true)
                    .build(),
                contentDescription = drama.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Fallback rich gradient canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B0A05),
                        Color(0xFF2E1208),
                        Color(0xFF0F0814),
                        Color(0xFF050505)
                    )
                )
                drawRect(brush)
            }
        }

        // Cinematic Canvas Overlay (Vignette, Light Glare, Film Lighting)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top and Bottom gradient overlays for high contrast reading
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.65f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.4f),
                        Color.Black.copy(alpha = 0.88f)
                    ),
                    startY = 0f,
                    endY = height
                )
            )

            // Dynamic camera light beam effect
            if (isPlaying) {
                val beamX = width * lightSweep
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            OrangePrimary.copy(alpha = 0.12f * pulseGlow),
                            CoinGold.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(beamX, height * 0.45f),
                        radius = width * 0.7f
                    ),
                    center = Offset(beamX, height * 0.45f),
                    radius = width * 0.7f
                )
            }
        }

        // Big Frosted Play/Pause indicator if paused
        AnimatedVisibility(
            visible = !isPlaying,
            enter = fadeIn() + scaleIn(initialScale = 0.7f),
            exit = fadeOut() + scaleOut(targetScale = 0.7f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Mainkan Video",
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )
            }
        }

        // Double-Tap Floating Heart Animation
        if (heartVisible) {
            LaunchedEffect(heartVisible) {
                delay(800)
                heartVisible = false
            }
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Suka",
                    tint = OrangeVibrant,
                    modifier = Modifier.size(96.dp)
                )
            }
        }

        // Bottom Video Progress Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 0.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = OrangePrimary,
                trackColor = Color.White.copy(alpha = 0.2f),
            )
        }
    }
}
