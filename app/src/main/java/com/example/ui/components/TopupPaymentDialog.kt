package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoinPackage
import com.example.data.model.PaymentChannel
import com.example.ui.theme.CimbRed
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.ShopeeOrange
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TngBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopupPaymentDialog(
    coinPackages: List<CoinPackage>,
    onSubmitReceipt: (CoinPackage, PaymentChannel, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPackage by remember { mutableStateOf(coinPackages.getOrNull(1) ?: coinPackages.first()) }
    var selectedChannel by remember { mutableStateOf(PaymentChannel.CIMB) }
    var referenceNumber by remember { mutableStateOf("") }
    var receiptNote by remember { mutableStateOf("") }

    val copyToClipboard = { text: String, label: String ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label disalin ke papan keratan!", Toast.LENGTH_SHORT).show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xF20A0A0A),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(TextMuted)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = GoldSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tambah Nilai Syiling",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_topup_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextSecondary
                    )
                }
            }

            Text(
                text = "Pilih pakej syiling dan buat bayaran pindahan manual ke akaun rasmi untuk pengesahan segera.",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            // Step 1: Pakej Syiling
            Text(
                text = "1. Pilih Pakej Syiling (MYR)",
                color = GoldLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(coinPackages) { pkg ->
                    val isSelected = pkg.id == selectedPackage.id
                    CoinPackageItem(
                        coinPackage = pkg,
                        isSelected = isSelected,
                        onClick = { selectedPackage = pkg }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step 2: Saluran Pembayaran
            Text(
                text = "2. Pilih Saluran Pembayaran Malaysia",
                color = GoldLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            PaymentChannelRow(
                selectedChannel = selectedChannel,
                onSelectChannel = { selectedChannel = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step 3: Maklumat Akaun Bank & Butang Salin
            AccountDetailsCard(
                channel = selectedChannel,
                amountMyr = selectedPackage.amountMyr,
                onCopy = copyToClipboard
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Step 4: Borang Pengesahan Resit
            Text(
                text = "3. Bukti Transaksi Pembayaran",
                color = GoldLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = referenceNumber,
                onValueChange = { referenceNumber = it },
                label = { Text("No. Rujukan / ID Transaksi Bank", fontSize = 13.sp) },
                placeholder = { Text("cth: CIMB-88391204 / TNG-549120", color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("topup_ref_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCanvas,
                    unfocusedContainerColor = DarkCanvas,
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = CrimsonPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = receiptNote,
                onValueChange = { receiptNote = it },
                label = { Text("Catatan / Masa Pindahan (Pilihan)", fontSize = 13.sp) },
                placeholder = { Text("cth: Pindahan jam 3:15 PM dari akaun Maybank", color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("topup_note_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCanvas,
                    unfocusedContainerColor = DarkCanvas,
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = CrimsonPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Submit Button
            Button(
                onClick = {
                    onSubmitReceipt(selectedPackage, selectedChannel, referenceNumber, receiptNote)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_receipt_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hantar Bukti Pembayaran (RM ${String.format("%.2f", selectedPackage.amountMyr)})",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Syiling akan dikreditkan secara automatik selepas semakan admin (1-5 minit).",
                    color = TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CoinPackageItem(
    coinPackage: CoinPackage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) GoldSecondary else DarkSurfaceBorder
    val bgColor = if (isSelected) GoldSecondary.copy(alpha = 0.12f) else DarkCanvas

    Box(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (coinPackage.tag.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (coinPackage.isPopular) CrimsonPrimary else GoldSecondary.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = coinPackage.tag,
                        color = if (coinPackage.isPopular) Color.White else GoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = GoldSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${coinPackage.coins}",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (coinPackage.bonusCoins > 0) {
                Text(
                    text = "+${coinPackage.bonusCoins} Bonus",
                    color = SuccessGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) CrimsonPrimary else DarkSurfaceElevated)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RM ${String.format("%.2f", coinPackage.amountMyr)}",
                    color = if (isSelected) Color.White else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PaymentChannelRow(
    selectedChannel: PaymentChannel,
    onSelectChannel: (PaymentChannel) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PaymentChannel.values().forEach { channel ->
            val isSelected = channel == selectedChannel
            val (brandColor, icon) = when (channel) {
                PaymentChannel.CIMB -> Pair(CimbRed, Icons.Default.AccountBalance)
                PaymentChannel.TNG_DUITNOW -> Pair(TngBlue, Icons.Default.PhoneAndroid)
                PaymentChannel.SHOPEEPAY -> Pair(ShopeeOrange, Icons.Default.ShoppingBag)
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectChannel(channel) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) brandColor.copy(alpha = 0.15f) else DarkCanvas
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) brandColor else DarkSurfaceBorder
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = channel.channelName,
                        tint = if (isSelected) brandColor else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (channel) {
                            PaymentChannel.CIMB -> "CIMB Bank"
                            PaymentChannel.TNG_DUITNOW -> "Touch 'n Go"
                            PaymentChannel.SHOPEEPAY -> "ShopeePay"
                        },
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AccountDetailsCard(
    channel: PaymentChannel,
    amountMyr: Double,
    onCopy: (String, String) -> Unit
) {
    val brandColor = when (channel) {
        PaymentChannel.CIMB -> CimbRed
        PaymentChannel.TNG_DUITNOW -> TngBlue
        PaymentChannel.SHOPEEPAY -> ShopeeOrange
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, brandColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCanvas)
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
                    text = channel.channelName,
                    color = brandColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Jumlah: RM ${String.format("%.2f", amountMyr)}",
                    color = GoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = channel.typeDesc,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Account Number Box with Copy Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Nombor Akaun / ID Pindahan:",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = channel.accountNumber,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Button(
                    onClick = { onCopy(channel.accountNumber, "Nombor Akaun ${channel.channelName}") },
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("copy_acc_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Salin",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salin", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Nama Penerima: ",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = channel.accountHolder,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
