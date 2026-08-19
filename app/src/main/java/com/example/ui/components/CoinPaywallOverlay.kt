package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DramaEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.CoinGold
import com.example.ui.theme.CoinGoldLight
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderStrong
import com.example.ui.theme.GlassModalBackground
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CoinPaywallOverlay(
    drama: DramaEntity,
    episode: EpisodeEntity,
    user: UserEntity?,
    autoUnlockNext: Boolean,
    onToggleAutoUnlock: () -> Unit,
    onUnlockClicked: () -> Unit,
    onTopupClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userBalance = user?.coinBalance ?: 0
    val hasEnoughCoins = userBalance >= episode.coinPrice

    val infiniteTransition = rememberInfiniteTransition(label = "paywall_glow")
    val lockPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Frosted Glass Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .border(
                    width = 1.dp,
                    color = GlassBorderStrong,
                    shape = RoundedCornerShape(28.dp)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = GlassModalBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Orange Badge Icon (Matching Frosted Glass mockup !)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .scale(lockPulse)
                        .clip(RoundedCornerShape(20.dp))
                        .background(OrangePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PriorityHigh,
                        contentDescription = "Terkunci",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Episod Terkunci",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sila gunakan ${episode.coinPrice} Syiling untuk menyambung tontonan episod ini.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Episode info pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ep ${episode.episodeNumber} • ${drama.title}",
                        color = CoinGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Balance display row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurfaceElevated)
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Baki Syiling Anda:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Baki",
                            tint = CoinGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$userBalance Syiling",
                            color = if (hasEnoughCoins) TextPrimary else OrangePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                if (hasEnoughCoins) {
                    // White Button with bold black text (from Frosted Glass mockup)
                    Button(
                        onClick = onUnlockClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("unlock_episode_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Buka Episod (${episode.coinPrice} Syiling)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Frosted Top-up Button
                    OutlinedButton(
                        onClick = onTopupClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("topup_secondary_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = GlassSurface,
                            contentColor = TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        Text(
                            text = "Tambah Nilai Syiling",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                } else {
                    // Orange Primary Action Button to Topup
                    Button(
                        onClick = onTopupClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("topup_coins_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tambah Nilai Syiling (Baki Tidak Cukup)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Auto-unlock toggle in frosted container
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurface)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Buka Episod Seterusnya Auto",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Tolak syiling lancar bila tonton",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                    Switch(
                        checked = autoUnlockNext,
                        onCheckedChange = { onToggleAutoUnlock() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = OrangePrimary,
                            checkedTrackColor = OrangePrimary.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = GlassSurfaceElevated
                        )
                    )
                }
            }
        }
    }
}
