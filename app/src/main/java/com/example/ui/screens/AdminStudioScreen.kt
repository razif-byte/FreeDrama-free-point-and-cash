package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DramaEntity
import com.example.data.model.TopupTransactionEntity
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.DramaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminStudioScreen(
    viewModel: DramaViewModel,
    modifier: Modifier = Modifier
) {
    val pendingTransactions by viewModel.pendingTransactions.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val dramas by viewModel.dramas.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Pengesahan Resit (${pendingTransactions.size})", "Muat Naik Drama", "Statistik")

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
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = GoldSecondary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Panel Pentadbir & Studio",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pengurusan Monetisasi & Kandungan",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceElevated,
            contentColor = CrimsonPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CrimsonPrimary
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
                            color = if (selectedTab == index) CrimsonPrimary else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> PendingReceiptsSection(
                pendingList = pendingTransactions,
                onApprove = { tx -> viewModel.approveTransaction(tx) },
                onReject = { id -> viewModel.rejectTransaction(id) }
            )
            1 -> DramaUploadSection(
                dramas = dramas,
                onUploadDrama = { title, genre, synopsis, totalEps, freeEps, coinPrice ->
                    viewModel.addNewDrama(title, genre, synopsis, totalEps, freeEps, coinPrice)
                },
                onDeleteDrama = { id -> viewModel.deleteDrama(id) }
            )
            2 -> AnalyticsSection(
                allTransactions = allTransactions,
                dramas = dramas
            )
        }
    }
}

@Composable
fun PendingReceiptsSection(
    pendingList: List<TopupTransactionEntity>,
    onApprove: (TopupTransactionEntity) -> Unit,
    onReject: (String) -> Unit
) {
    if (pendingList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Semua Resit Telah Diproses!",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tiada transaksi pembayaran yang sedang menunggu kelulusan ketika ini.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pendingList) { tx ->
                PendingTransactionCard(
                    tx = tx,
                    onApprove = { onApprove(tx) },
                    onReject = { onReject(tx.id) }
                )
            }
        }
    }
}

@Composable
fun PendingTransactionCard(
    tx: TopupTransactionEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()) }
    val dateStr = dateFormat.format(Date(tx.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldSecondary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GoldSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tx.paymentMethod,
                        color = GoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "RM ${String.format("%.2f", tx.amountMyr)}",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pakej: ${tx.packageName} (${tx.coinsReward + tx.bonusCoins} Syiling)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "No. Rujukan: ${tx.referenceNumber}",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            if (tx.receiptNote.isNotBlank()) {
                Text(
                    text = "Catatan Pengguna: \"${tx.receiptNote}\"",
                    color = TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Text(
                text = "Masa Dihantar: $dateStr",
                color = TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons (Lulus / Tolak)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("admin_reject_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tolak", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(40.dp)
                        .testTag("admin_approve_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = DarkCanvas,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Luluskan (+${tx.coinsReward + tx.bonusCoins})",
                        color = DarkCanvas,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DramaUploadSection(
    dramas: List<DramaEntity>,
    onUploadDrama: (String, String, String, Int, Int, Int) -> Unit,
    onDeleteDrama: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Romantik Korporat") }
    var synopsis by remember { mutableStateOf("") }
    var totalEpisodes by remember { mutableIntStateOf(10) }
    var freeEpisodesCount by remember { mutableIntStateOf(3) }
    var coinPrice by remember { mutableIntStateOf(15) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VideoCall,
                        contentDescription = null,
                        tint = CrimsonPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Muat Naik Siri Drama Baru",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tajuk Drama", fontSize = 12.sp) },
                    placeholder = { Text("cth: Cinta Rahsia Sang CEO", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("drama_title_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCanvas,
                        unfocusedContainerColor = DarkCanvas,
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre Drama", fontSize = 12.sp) },
                    placeholder = { Text("Romantik, Dendam, Aksi", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCanvas,
                        unfocusedContainerColor = DarkCanvas,
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = synopsis,
                    onValueChange = { synopsis = it },
                    label = { Text("Sinopsis Drama", fontSize = 12.sp) },
                    placeholder = { Text("Ringkasan jalan cerita penuh konflik...", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCanvas,
                        unfocusedContainerColor = DarkCanvas,
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Monetization Config
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Jumlah Episod: $totalEpisodes", color = TextSecondary, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = { if (totalEpisodes > 5) totalEpisodes -= 5 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceBorder)
                            ) {
                                Text("-5", fontSize = 10.sp)
                            }
                            Button(
                                onClick = { totalEpisodes += 5 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceBorder)
                            ) {
                                Text("+5", fontSize = 10.sp)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Episod Percuma: $freeEpisodesCount", color = SuccessGreen, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = { if (freeEpisodesCount > 1) freeEpisodesCount -= 1 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceBorder)
                            ) {
                                Text("-1", fontSize = 10.sp)
                            }
                            Button(
                                onClick = { if (freeEpisodesCount < totalEpisodes) freeEpisodesCount += 1 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceBorder)
                            ) {
                                Text("+1", fontSize = 10.sp)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Harga Syiling: $coinPrice", color = GoldLight, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = { if (coinPrice > 5) coinPrice -= 5 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceBorder)
                            ) {
                                Text("-5", fontSize = 10.sp)
                            }
                            Button(
                                onClick = { coinPrice += 5 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceBorder)
                            ) {
                                Text("+5", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onUploadDrama(title, genre, synopsis, totalEpisodes, freeEpisodesCount, coinPrice)
                        title = ""
                        synopsis = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_drama_upload_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text(
                        text = "Terbitkan Siri Drama Ini",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Existing Dramas List
        Text(
            text = "Siri Drama Sedia Ada (${dramas.size})",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        dramas.forEach { drama ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = drama.title,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${drama.genre} • ${drama.totalEpisodes} Episod",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = { onDeleteDrama(drama.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Padam",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsSection(
    allTransactions: List<TopupTransactionEntity>,
    dramas: List<DramaEntity>
) {
    val totalRevenue = allTransactions
        .filter { it.status == "BERJAYA" }
        .sumOf { it.amountMyr }

    val totalCoinsIssued = allTransactions
        .filter { it.status == "BERJAYA" }
        .sumOf { it.coinsReward + it.bonusCoins }

    val approvedCount = allTransactions.count { it.status == "BERJAYA" }
    val pendingCount = allTransactions.count { it.status == "MENUNGGU_PENGESAHAN" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnalyticsCard(
                title = "Jumlah Hasil (MYR)",
                value = "RM ${String.format("%.2f", totalRevenue)}",
                color = SuccessGreen,
                icon = Icons.Default.MonetizationOn,
                modifier = Modifier.weight(1f)
            )
            AnalyticsCard(
                title = "Syiling Dikeluarkan",
                value = "$totalCoinsIssued",
                color = GoldSecondary,
                icon = Icons.Default.MonetizationOn,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnalyticsCard(
                title = "Siri Drama",
                value = "${dramas.size}",
                color = CrimsonPrimary,
                icon = Icons.Default.Movie,
                modifier = Modifier.weight(1f)
            )
            AnalyticsCard(
                title = "Resit Menunggu",
                value = "$pendingCount",
                color = GoldLight,
                icon = Icons.Default.ReceiptLong,
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Prestasi Platform Penstriman",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Model Monetisasi: Freemium Paywall (Episod 1-3 Percuma, Episod 4+ Beli Syiling)",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "• Saluran Kutipan: Bank CIMB, Touch 'n Go (DuitNow), ShopeePay",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "• Jumlah Transaksi Berjaya: $approvedCount transaksi",
                    color = SuccessGreen,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
