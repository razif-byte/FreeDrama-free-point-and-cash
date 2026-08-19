package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DramaEntity
import com.example.data.model.EpisodeEntity
import com.example.ui.components.CinematicDramaCanvas
import com.example.ui.components.CoinPaywallOverlay
import com.example.ui.components.CommentsSheet
import com.example.ui.components.DramaReviewsSheet
import com.example.ui.components.EpisodeSelectorSheet
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.ShopVouchersDialog
import com.example.ui.components.TopupPaymentDialog
import com.example.ui.components.WatermarkBadge
import com.example.ui.theme.CoinGold
import com.example.ui.theme.CoinGoldLight
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderStrong
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceActive
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
fun FeedScreen(
    viewModel: DramaViewModel,
    onNavigateToExplore: () -> Unit,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDrama by viewModel.selectedDrama.collectAsState()
    val episodes by viewModel.currentEpisodes.collectAsState()
    val currentEpisodeIndex by viewModel.currentEpisodeIndex.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val unlockedEpisodeIds by viewModel.unlockedEpisodeIds.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val autoUnlockNext by viewModel.autoUnlockNext.collectAsState()

    val isEpisodeSelectorOpen by viewModel.isEpisodeSelectorOpen.collectAsState()
    val isCommentsOpen by viewModel.isCommentsOpen.collectAsState()
    val isReviewsSheetOpen by viewModel.isReviewsSheetOpen.collectAsState()
    val isTopupDialogOpen by viewModel.isTopupDialogOpen.collectAsState()
    val isShopVoucherDialogOpen by viewModel.isShopVoucherDialogOpen.collectAsState()
    val isAdDialogOpen by viewModel.isAdDialogOpen.collectAsState()
    val activeAd by viewModel.activeAd.collectAsState()
    val userVouchers by viewModel.userVouchers.collectAsState()

    val activeEpisode: EpisodeEntity? = episodes.getOrNull(currentEpisodeIndex)

    var isBookmarked by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var selectedFeedTab by remember { mutableStateOf("Untuk Anda") }

    val openShopWeb = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DramaViewModel.SHOP_URL))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
        }
    }

    if (selectedDrama == null || activeEpisode == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkCanvas),
            contentAlignment = Alignment.Center
        ) {
            Text("Memuatkan drama pendek...", color = TextSecondary)
        }
        return
    }

    val currentDrama = selectedDrama!!
    val isEpisodeUnlocked = activeEpisode.isFree || unlockedEpisodeIds.contains(activeEpisode.id)
    val isLiked by viewModel.isLiked(activeEpisode.id).collectAsState(initial = false)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    dragOffset += delta
                },
                onDragStopped = {
                    if (dragOffset < -60f) {
                        viewModel.nextEpisode()
                    } else if (dragOffset > 60f) {
                        viewModel.previousEpisode()
                    }
                    dragOffset = 0f
                }
            )
    ) {
        // 1. Video Player Canvas
        CinematicDramaCanvas(
            drama = currentDrama,
            episode = activeEpisode,
            isPlaying = isPlaying && isEpisodeUnlocked,
            onTogglePlayPause = {
                if (isEpisodeUnlocked) {
                    viewModel.togglePlayPause()
                }
            },
            onDoubleTapLike = {
                viewModel.toggleLike(activeEpisode, isLiked)
            }
        )

        // 2. Paywall Lock Overlay if episode requires unlock
        if (!isEpisodeUnlocked) {
            CoinPaywallOverlay(
                drama = currentDrama,
                episode = activeEpisode,
                user = currentUser,
                autoUnlockNext = autoUnlockNext,
                onToggleAutoUnlock = { viewModel.toggleAutoUnlock() },
                onUnlockClicked = { viewModel.unlockCurrentEpisode(activeEpisode) },
                onTopupClicked = { viewModel.setTopupDialogOpen(true) }
            )
        }

        // 3. Top Header Bar (Frosted Glass Design Header + Shop Button + Coin Pill)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Feed Tabs (Mengikuti / Untuk Anda)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Mengikuti",
                    color = if (selectedFeedTab == "Mengikuti") TextPrimary else TextMuted,
                    fontSize = 14.sp,
                    fontWeight = if (selectedFeedTab == "Mengikuti") FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.clickable { selectedFeedTab = "Mengikuti" }
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedFeedTab = "Untuk Anda" }
                ) {
                    Text(
                        text = "Untuk Anda",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(2.5.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            // Top Action Group: Shop Button + Frosted Coin Balance Pill
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
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("feed_shop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Shop",
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Shop",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Frosted Coin Balance Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .clickable { viewModel.setTopupDialogOpen(true) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("feed_coin_balance_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(CoinGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${currentUser?.coinBalance ?: 0}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 4. Right Sidebar Action Buttons (Frosted Glass circular action buttons)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Heart (Like) Action
            FrostedSidebarAction(
                icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = "${activeEpisode.likesCount}",
                iconTint = if (isLiked) OrangeVibrant else Color.White,
                onClick = { viewModel.toggleLike(activeEpisode, isLiked) },
                testTag = "feed_like_button"
            )

            // Comments Action
            FrostedSidebarAction(
                icon = Icons.Default.ChatBubble,
                label = "${activeEpisode.commentsCount}",
                iconTint = Color.White,
                onClick = { viewModel.setCommentsOpen(true) },
                testTag = "feed_comments_button"
            )

            // Drama Star Rating & Review Action
            FrostedSidebarAction(
                icon = Icons.Default.Star,
                label = "${currentDrama.rating}★",
                iconTint = CoinGold,
                onClick = { viewModel.setReviewsSheetOpen(true) },
                testTag = "feed_reviews_button"
            )

            // Shop Loyalty Vouchers Action
            FrostedSidebarAction(
                icon = Icons.Default.Loyalty,
                label = "Baucar",
                iconTint = OrangeLight,
                onClick = { viewModel.setShopVoucherDialogOpen(true) },
                testTag = "feed_voucher_button"
            )

            // Free Coins / Reward Ad Action
            FrostedSidebarAction(
                icon = Icons.Default.CardGiftcard,
                label = "+Syiling",
                iconTint = SuccessGreen,
                onClick = { viewModel.showRewardedAd() },
                testTag = "feed_reward_ad_button"
            )

            // Episode Selector Action
            FrostedSidebarAction(
                icon = if (isEpisodeUnlocked) Icons.Default.VideoLibrary else Icons.Default.LockOpen,
                label = if (isEpisodeUnlocked) "Ep ${activeEpisode.episodeNumber}/${currentDrama.totalEpisodes}" else "Buka",
                iconTint = Color.White,
                isHighlighted = !isEpisodeUnlocked,
                onClick = {
                    if (!isEpisodeUnlocked) {
                        viewModel.unlockCurrentEpisode(activeEpisode)
                    } else {
                        viewModel.setEpisodeSelectorOpen(true)
                    }
                },
                testTag = "feed_episode_drawer_button"
            )

            // Up / Down Quick Navigation Arrows
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = { viewModel.previousEpisode() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Episod Sebelumnya",
                        tint = if (currentEpisodeIndex > 0) Color.White else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = { viewModel.nextEpisode() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Episod Seterusnya",
                        tint = if (currentEpisodeIndex < episodes.size - 1) Color.White else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 5. Bottom Metadata Overlay (Frosted Glass Layout)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .navigationBarsPadding()
                .padding(start = 16.dp, bottom = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Episode tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(OrangePrimary.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "EPISOD ${activeEpisode.episodeNumber}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Malay Dubbing Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E3A8A).copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFF60A5FA).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🇲🇾 DUBBING MELAYU",
                        color = Color(0xFFBFDBFE),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Trademark & Watermark = "RazifApps@Nasadef" link = "https://nasadef.com.my"
                WatermarkBadge(isOverlayStyle = true)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Drama Title
            Text(
                text = currentDrama.title,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Synopsis Snippet
            Text(
                text = if (activeEpisode.teaserText.isNotBlank()) activeEpisode.teaserText else currentDrama.synopsis,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Studio Info row & Direct External Video Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "${currentDrama.genre} • ${currentDrama.rating}★ (${currentDrama.reviewsCount} Ulasan)",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )

                if (activeEpisode.videoUrl.isNotBlank() && activeEpisode.videoUrl.startsWith("http")) {
                    val context = LocalContext.current
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(activeEpisode.videoUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Fallback
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (activeEpisode.videoUrl.contains("youtu")) "▶ Video YouTube" else "▶ Video Drive",
                            color = OrangeVibrant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 6. Bottom Sheets & Dialogs
        if (isEpisodeSelectorOpen) {
            EpisodeSelectorSheet(
                drama = currentDrama,
                episodes = episodes,
                currentEpisodeIndex = currentEpisodeIndex,
                unlockedEpisodeIds = unlockedEpisodeIds,
                onSelectEpisode = { idx -> viewModel.setEpisodeIndex(idx) },
                onDismiss = { viewModel.setEpisodeSelectorOpen(false) }
            )
        }

        if (isCommentsOpen) {
            CommentsSheet(
                episode = activeEpisode,
                commentsFlow = viewModel.getCommentsForEpisode(activeEpisode.id),
                onSendComment = { text -> viewModel.addComment(activeEpisode.id, text) },
                onDismiss = { viewModel.setCommentsOpen(false) }
            )
        }

        if (isReviewsSheetOpen) {
            DramaReviewsSheet(
                drama = currentDrama,
                reviewsFlow = viewModel.getReviewsForDrama(currentDrama.id),
                onSubmitReview = { rating, text -> viewModel.submitReview(currentDrama.id, rating, text) },
                onDismiss = { viewModel.setReviewsSheetOpen(false) }
            )
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

        if (isTopupDialogOpen) {
            TopupPaymentDialog(
                coinPackages = viewModel.coinPackages,
                onSubmitReceipt = { pkg, channel, ref, note ->
                    viewModel.submitTopupReceipt(pkg, channel, ref, note)
                },
                onDismiss = { viewModel.setTopupDialogOpen(false) }
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
}

@Composable
fun FrostedSidebarAction(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    onClick: () -> Unit,
    testTag: String,
    isHighlighted: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        val containerBg = if (isHighlighted) OrangePrimary else Color.White.copy(alpha = 0.12f)
        val borderColor = if (isHighlighted) OrangeDark else Color.White.copy(alpha = 0.15f)

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(containerBg)
                .border(1.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isHighlighted) OrangeLight else Color.White,
            fontSize = 9.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
        )
    }
}
