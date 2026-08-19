package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.ShopVouchersDialog
import com.example.ui.components.WatermarkBadge
import com.example.ui.theme.CoinGold
import com.example.ui.theme.CoinGoldLight
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderStrong
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangeVibrant
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.DramaViewModel

@Composable
fun ProfileScreen(
    viewModel: DramaViewModel,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val unlockedEpisodeIds by viewModel.unlockedEpisodeIds.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val isShopVoucherDialogOpen by viewModel.isShopVoucherDialogOpen.collectAsState()
    val isAdDialogOpen by viewModel.isAdDialogOpen.collectAsState()
    val activeAd by viewModel.activeAd.collectAsState()
    val userVouchers by viewModel.userVouchers.collectAsState()

    val openShopWeb = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DramaViewModel.SHOP_URL))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
        }
    }

    val openTrademarkWeb = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DramaViewModel.TRADEMARK_URL))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCanvas)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profil Pengguna",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button caption = "Shop" button link = "https://nasadef-website.web.app/"
                Button(
                    onClick = openShopWeb,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("profile_shop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Shop",
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Shop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                WatermarkBadge()
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // User Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(OrangePrimary, CoinGold))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentUser?.username ?: "Razif Penonton VIP",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentUser?.email ?: "razifmake@gmail.com",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CoinGold.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VIP MEMBER",
                                color = CoinGoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Stats Row (Coins, Reward Points, Episodes)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToWallet() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GlassSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = CoinGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentUser?.coinBalance ?: 0}",
                        color = CoinGoldLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Syiling",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.setShopVoucherDialogOpen(true) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GlassSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentUser?.rewardPoints ?: 0}",
                        color = OrangeLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Mata Shop",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GlassSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${unlockedEpisodeIds.size}",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Ep Dibuka",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Action Tiles: Shop Voucher & Rewarded Ads
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, OrangePrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.setShopVoucherDialogOpen(true) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Loyalty,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Tebus Baucar", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Guna di Nasadef Shop", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFF4285F4).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.showRewardedAd() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = Color(0xFF8AB4F8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Tonton Iklan", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("+Syiling & Mata", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Admin Mode Switch Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CoinGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isAdminMode) CoinGold.copy(alpha = 0.12f) else GlassSurfaceElevated
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = CoinGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mod Pentadbir (Admin Studio)",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Semakan resit CIMB/TNG & muat naik video",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = isAdminMode,
                    onCheckedChange = { viewModel.toggleAdminMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CoinGold,
                        checkedTrackColor = CoinGold.copy(alpha = 0.4f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("admin_mode_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trademark & Developer Footer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorderStrong, RoundedCornerShape(16.dp))
                .clickable { openTrademarkWeb() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = DramaViewModel.TRADEMARK_TEXT,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hak Cipta Terpelihara. Lawati laman rasmi kami di ${DramaViewModel.TRADEMARK_URL}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }

    if (isShopVoucherDialogOpen) {
        ShopVouchersDialog(
            user = currentUser,
            availableRewards = viewModel.availableShopVouchers,
            claimedVouchers = userVouchers,
            onClaimVoucher = { reward -> viewModel.claimShopVoucher(reward) },
            onDismiss = { viewModel.setShopVoucherDialogOpen(false) }
        )
    }

    if (isAdDialogOpen && activeAd != null) {
        RewardedAdDialog(
            ad = activeAd!!,
            onRewardEarned = { viewModel.completeRewardedAd(activeAd!!) },
            onDismiss = { viewModel.dismissAd() }
        )
    }
}
