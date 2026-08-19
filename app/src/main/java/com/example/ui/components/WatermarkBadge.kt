package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.OrangePrimary
import com.example.ui.viewmodel.DramaViewModel

@Composable
fun WatermarkBadge(
    modifier: Modifier = Modifier,
    isOverlayStyle: Boolean = false
) {
    val context = LocalContext.current

    val containerBg = if (isOverlayStyle) {
        Color.Black.copy(alpha = 0.55f)
    } else {
        Color.White.copy(alpha = 0.08f)
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(containerBg)
            .border(1.dp, GlassBorder, CircleShape)
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DramaViewModel.TRADEMARK_URL))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback
                }
            }
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("trademark_watermark_badge"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = "Trademark",
            tint = CoinGold,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = DramaViewModel.TRADEMARK_TEXT,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp
        )
    }
}
