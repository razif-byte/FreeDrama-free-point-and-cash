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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.TopupTransactionEntity
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.ShopVouchersDialog
import com.example.ui.components.TopupPaymentDialog
import com.example.ui.components.WatermarkBadge
import com.example.ui.theme.CoinGold
import com.example.ui.theme.CoinGoldLight
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderStrong
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.OrangeDark
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.DramaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    viewModel: DramaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val isTopupDialogOpen by viewModel.isTopupDialogOpen.collectAsState()
    val isShopVoucherDialogOpen by viewModel.isShopVoucherDialogOpen.collectAsState()
    val isAdDialogOpen by viewModel.isAdDialogOpen.collectAsState()
    val activeAd by viewModel.activeAd.collectAsState()
    val userVouchers by viewModel.userVouchers.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Semua", "Menunggu", "Berjaya", "Ditolak")

    val openShopWeb = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DramaViewModel.SHOP_URL))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
        }
    }

    val filteredTransactions = when (selectedTab) {
        1 -> allTransactions.filter { it.status == "MENUNGGU_PENGESAHAN" }
        2 -> allTransactions.filter { it.status == "BERJAYA" }
        3 -> allTransactions.filter { it.status == "DITOLAK" }
        else -> allTransactions
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCanvas)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dompet & Ganjaran",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

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
                        .testTag("wallet_shop_button")
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

        // Wallet Balance Card with Frosted Glass Styling
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(CoinGold, OrangePrimary)),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Baki Syiling Semasa",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = CoinGold,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${currentUser?.coinBalance ?: 0}",
                                color = CoinGoldLight,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = " Syiling",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                            )
                        }
                    }

                    // Loyalty Reward Points Summary Box
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.setShopVoucherDialogOpen(true) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Mata Shop",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = CoinGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${currentUser?.rewardPoints ?: 0}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons Row (Tambah Nilai + Tebus Baucar + Tonton Iklan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.setTopupDialogOpen(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("wallet_topup_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tambah Nilai",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.setShopVoucherDialogOpen(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("wallet_vouchers_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OrangeLight
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Loyalty,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tebus Baucar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Watch Ads for Free Coins & Points Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF4285F4).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFF4285F4).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { viewModel.showRewardedAd() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color(0xFF8AB4F8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tonton Iklan: Percuma +10 Syiling & +50 Mata",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "Tonton >",
                        color = Color(0xFF8AB4F8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction History Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sejarah Transaksi & Resit",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = GlassSurface,
            contentColor = OrangePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = OrangePrimary,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) OrangePrimary else TextSecondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tiada rekod transaksi dijumpai.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { tx ->
                    TransactionItemCard(transaction = tx)
                }
            }
        }
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
fun TransactionItemCard(transaction: TopupTransactionEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("ms", "MY")) }
    val formattedDate = remember(transaction.createdAt) { dateFormat.format(Date(transaction.createdAt)) }

    val (statusColor, statusIcon, statusText) = when (transaction.status) {
        "BERJAYA" -> Triple(SuccessGreen, Icons.Default.CheckCircle, "BERJAYA")
        "DITOLAK" -> Triple(ErrorRed, Icons.Default.Error, "DITOLAK")
        else -> Triple(WarningAmber, Icons.Default.HourglassEmpty, "MENUNGGU PENGESAHAN")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.packageName,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "RM${String.format(Locale.US, "%.2f", transaction.amountMyr)}",
                    color = OrangeLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${transaction.paymentMethod} • Ref: ${transaction.referenceNumber}",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = formattedDate,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
