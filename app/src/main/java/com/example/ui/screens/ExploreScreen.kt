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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.DramaEntity
import com.example.ui.components.DramaReviewsSheet
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
fun ExploreScreen(
    viewModel: DramaViewModel,
    onDramaSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dramas by viewModel.dramas.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val userVouchers by viewModel.userVouchers.collectAsState()
    val isReviewsSheetOpen by viewModel.isReviewsSheetOpen.collectAsState()
    val isShopVoucherDialogOpen by viewModel.isShopVoucherDialogOpen.collectAsState()
    val isAdDialogOpen by viewModel.isAdDialogOpen.collectAsState()
    val activeAd by viewModel.activeAd.collectAsState()
    val selectedDrama by viewModel.selectedDrama.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("Semua") }

    val genres = listOf("Semua", "Dubbing Melayu 🇲🇾", "Romantik Korporat", "Dendam & Aksi", "Cinta Kontrak", "Trending 🔥")

    val filteredDramas = dramas.filter { drama ->
        val matchesSearch = drama.title.contains(searchQuery, ignoreCase = true) ||
                drama.synopsis.contains(searchQuery, ignoreCase = true)
        val matchesGenre = when (selectedGenre) {
            "Semua" -> true
            "Dubbing Melayu 🇲🇾" -> drama.title.contains("Dubbing", ignoreCase = true) || drama.genre.contains("Melayu", ignoreCase = true)
            "Trending 🔥" -> drama.isFeatured
            else -> drama.genre.contains(selectedGenre, ignoreCase = true)
        }
        matchesSearch && matchesGenre
    }

    val openShopWeb = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DramaViewModel.SHOP_URL))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCanvas)
            .padding(top = 16.dp)
    ) {
        // Top Action Bar: Search + Shop Button + Watermark
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Terokai Drama",
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
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("explore_shop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Shop",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Shop",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Watermark & Trademark = "RazifApps@Nasadef"
                WatermarkBadge()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari drama, genre, atau sinopsis...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = TextSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("explore_search_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GlassSurfaceElevated,
                unfocusedContainerColor = GlassSurface,
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = GlassBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Genre Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                val isSelected = genre == selectedGenre
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) OrangePrimary else GlassSurface)
                        .border(
                            1.dp,
                            if (isSelected) OrangeDark else GlassBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedGenre = genre }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = genre,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of Dramas with Google Ads Sponsored Banner
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Google Ads Sponsored Monetization Banner
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E1B4B), Color(0xFF311006))
                            )
                        )
                        .border(1.dp, GlassBorderStrong, RoundedCornerShape(18.dp))
                        .clickable { viewModel.showRewardedAd() },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF4285F4).copy(alpha = 0.3f))
                                        .border(1.dp, Color(0xFF4285F4), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "IKLAN GOOGLE TAJAAN",
                                        color = Color(0xFF8AB4F8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Nasadef Store",
                                    color = OrangeLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Tonton Iklan: Dapat +10 Syiling & +50 Mata Shop Percuma!",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.showRewardedAd() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Tonton", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Drama Items
            items(filteredDramas, key = { it.id }) { drama ->
                ExploreDramaCard(
                    drama = drama,
                    onClick = {
                        viewModel.selectDrama(drama)
                        onDramaSelected()
                    },
                    onReviewClick = {
                        viewModel.selectDrama(drama)
                        viewModel.setReviewsSheetOpen(true)
                    }
                )
            }
        }
    }

    if (isReviewsSheetOpen && selectedDrama != null) {
        DramaReviewsSheet(
            drama = selectedDrama!!,
            reviewsFlow = viewModel.getReviewsForDrama(selectedDrama!!.id),
            onSubmitReview = { rating, text -> viewModel.submitReview(selectedDrama!!.id, rating, text) },
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

    if (isAdDialogOpen && activeAd != null) {
        RewardedAdDialog(
            ad = activeAd!!,
            onRewardEarned = { viewModel.completeRewardedAd(activeAd!!) },
            onDismiss = { viewModel.dismissAd() }
        )
    }
}

@Composable
fun ExploreDramaCard(
    drama: DramaEntity,
    onClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("drama_card_${drama.id}"),
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated)
    ) {
        Column {
            // Poster Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF20162B), Color(0xFF0F0F0F))
                        )
                    )
            ) {
                // Mock poster gradient art
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF371B58), Color(0xFF1E1035), Color(0xFF0A0A0A))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = OrangeVibrant,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "${drama.totalEpisodes} Episod",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Top Tags: Genre & Rating
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(OrangePrimary.copy(alpha = 0.9f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = drama.genre,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Rating Badge (Clickable to open reviews)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .clickable { onReviewClick() }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${drama.rating}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Drama Details
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = drama.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = drama.synopsis,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${drama.viewsCount / 1000}k tontonan",
                        color = TextMuted,
                        fontSize = 9.sp
                    )

                    Text(
                        text = "Ulas (${drama.reviewsCount})",
                        color = OrangeLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onReviewClick() }
                    )
                }
            }
        }
    }
}
