package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_default_01",
    val username: String = "Razif Penonton VIP",
    val email: String = "razifmake@gmail.com",
    val avatarUrl: String = "",
    val coinBalance: Int = 30, // Coins for unlocking episodes
    val rewardPoints: Int = 120, // Accumulated points for voucher redemption at Shop
    val isVip: Boolean = false,
    val totalEpisodesWatched: Int = 12,
    val totalAdsWatched: Int = 3,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dramas")
data class DramaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val genre: String, // "Romantik CEO", "Dendam & Kuasa", "Cinta Kontrak", "Aksi Samseng"
    val synopsis: String,
    val posterResName: String,
    val bannerUrl: String = "",
    val totalEpisodes: Int = 10,
    val rating: Double = 4.9,
    val reviewsCount: Int = 28,
    val viewsCount: Int = 184500,
    val isFeatured: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey val id: String,
    val dramaId: String,
    val episodeNumber: Int,
    val title: String,
    val videoUrl: String,
    val durationSeconds: Int = 90,
    val isFree: Boolean = true, // Ep 1-3 Free, Ep 4+ Paid
    val coinPrice: Int = 15,
    val likesCount: Int = 1240,
    val commentsCount: Int = 86,
    val teaserText: String = ""
)

@Entity(tableName = "unlocked_episodes")
data class UnlockedEpisodeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val dramaId: String,
    val episodeId: String,
    val coinsSpent: Int,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "topup_transactions")
data class TopupTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val packageName: String,
    val amountMyr: Double,
    val coinsReward: Int,
    val bonusCoins: Int,
    val paymentMethod: String, // "CIMB Bank", "Touch 'n Go (DuitNow)", "ShopeePay"
    val accountNumber: String,
    val referenceNumber: String,
    val receiptNote: String,
    val status: String, // "MENUNGGU_PENGESAHAN", "BERJAYA", "DITOLAK"
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val episodeId: String,
    val username: String,
    val avatarUrl: String = "",
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val likesCount: Int = 5
)

@Entity(tableName = "likes")
data class LikeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val episodeId: String
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val dramaId: String,
    val userId: String,
    val username: String,
    val rating: Int, // 1 to 5 stars
    val reviewText: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "claimed_vouchers")
data class ClaimedVoucherEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val voucherTitle: String,
    val voucherCode: String,
    val discountDescription: String,
    val pointsCost: Int,
    val shopUrl: String = "https://nasadef-website.web.app/",
    val claimedAt: Long = System.currentTimeMillis(),
    val isUsed: Boolean = false
)

data class ShopVoucherReward(
    val id: String,
    val title: String,
    val discountDesc: String,
    val pointsRequired: Int,
    val codePrefix: String,
    val badge: String = "Popular"
)

data class SponsoredAd(
    val id: String,
    val title: String,
    val sponsorName: String,
    val description: String,
    val actionText: String,
    val targetUrl: String,
    val rewardCoins: Int = 10,
    val rewardPoints: Int = 50,
    val durationSeconds: Int = 10
)

data class CoinPackage(
    val id: String,
    val name: String,
    val amountMyr: Double,
    val coins: Int,
    val bonusCoins: Int,
    val isPopular: Boolean = false,
    val tag: String = ""
)

enum class PaymentChannel(
    val channelName: String,
    val accountNumber: String,
    val accountHolder: String,
    val typeDesc: String,
    val iconName: String
) {
    CIMB(
        channelName = "Bank CIMB",
        accountNumber = "7016657934",
        accountHolder = "DRAMASHORT ENTERPRISE",
        typeDesc = "Perbankan Dalam Talian / Pindahan Segera",
        iconName = "cimb"
    ),
    TNG_DUITNOW(
        channelName = "Touch 'n Go (DuitNow)",
        accountNumber = "170997196734",
        accountHolder = "DRAMASHORT ENTERPRISE (DuitNow ID)",
        typeDesc = "DuitNow QR / DuitNow Transfer",
        iconName = "tng"
    ),
    SHOPEEPAY(
        channelName = "ShopeePay",
        accountNumber = "64485893880474",
        accountHolder = "DRAMASHORT ENTERPRISE",
        typeDesc = "Pindahan ShopeePay / QR Wallet",
        iconName = "shopeepay"
    )
}
