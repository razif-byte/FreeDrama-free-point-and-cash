package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ClaimedVoucherEntity
import com.example.data.model.CoinPackage
import com.example.data.model.CommentEntity
import com.example.data.model.DramaEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.LikeEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.ShopVoucherReward
import com.example.data.model.SponsoredAd
import com.example.data.model.TopupTransactionEntity
import com.example.data.model.UnlockedEpisodeEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class DramaRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val dramaDao = db.dramaDao()
    private val episodeDao = db.episodeDao()
    private val unlockedDao = db.unlockedEpisodeDao()
    private val transactionDao = db.transactionDao()
    private val commentDao = db.commentDao()
    private val likeDao = db.likeDao()
    private val reviewDao = db.reviewDao()
    private val voucherDao = db.claimedVoucherDao()

    val currentUser: Flow<UserEntity?> = userDao.getUser("user_default_01")
    val allDramas: Flow<List<DramaEntity>> = dramaDao.getAllDramas()
    val allTransactions: Flow<List<TopupTransactionEntity>> = transactionDao.getAllTransactions()
    val pendingTransactions: Flow<List<TopupTransactionEntity>> = transactionDao.getPendingTransactions()
    val userVouchers: Flow<List<ClaimedVoucherEntity>> = voucherDao.getUserVouchers("user_default_01")

    fun getEpisodesForDrama(dramaId: String): Flow<List<EpisodeEntity>> =
        episodeDao.getEpisodesForDrama(dramaId)

    fun getUnlockedEpisodes(userId: String = "user_default_01"): Flow<List<UnlockedEpisodeEntity>> =
        unlockedDao.getUnlockedEpisodes(userId)

    fun isEpisodeUnlocked(userId: String = "user_default_01", episodeId: String): Flow<Boolean> =
        unlockedDao.isEpisodeUnlocked(userId, episodeId)

    fun getCommentsForEpisode(episodeId: String): Flow<List<CommentEntity>> =
        commentDao.getCommentsForEpisode(episodeId)

    fun isLiked(userId: String = "user_default_01", episodeId: String): Flow<Boolean> =
        likeDao.isLiked(userId, episodeId)

    fun getReviewsForDrama(dramaId: String): Flow<List<ReviewEntity>> =
        reviewDao.getReviewsForDrama(dramaId)

    fun getUserReviewForDrama(dramaId: String, userId: String = "user_default_01"): Flow<ReviewEntity?> =
        reviewDao.getUserReviewForDrama(dramaId, userId)

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val user = userDao.getUserSync("user_default_01")
        if (user == null) {
            userDao.insertUser(
                UserEntity(
                    id = "user_default_01",
                    username = "Razif Penonton VIP",
                    email = "razifmake@gmail.com",
                    coinBalance = 35, // Starter free coins
                    rewardPoints = 140, // Starter loyalty reward points for shop
                    isVip = false,
                    totalEpisodesWatched = 4,
                    totalAdsWatched = 2
                )
            )

            // Seed Initial Dramas
            val drama1 = DramaEntity(
                id = "drama_ceo_gadis",
                title = "CEO Terpikat Gadis Kampung",
                genre = "Romantik Korporat",
                synopsis = "Seorang gadis kampung yang naif terpaksa berhijrah ke Kuala Lumpur demi menyara rawatan ibunya. Pertemuan tidak sengaja dengan Tengku Zaril, CEO megah bernilai bilion ringgit, merubah segala takdir apabila satu perkahwinan rahsia termeterai.",
                posterResName = "poster_ceo_gadis_1787165470282",
                totalEpisodes = 10,
                rating = 4.9,
                reviewsCount = 48,
                viewsCount = 342100,
                isFeatured = true
            )

            val drama2 = DramaEntity(
                id = "drama_jutawan_rahsia",
                title = "Dendam Sang Jutawan Tersembunyi",
                genre = "Dendam & Aksi",
                synopsis = "Dituduh membunuh bapanya sendiri dan diusir dari keluarga konglomerat, Dani Mikail bangkit kembali selepas 5 tahun membawa empayar kewangan gelap untuk membalas dendam kepada mereka yang menganiayanya.",
                posterResName = "poster_jutawan_rahsia_1787165486763",
                totalEpisodes = 8,
                rating = 4.8,
                reviewsCount = 32,
                viewsCount = 289400,
                isFeatured = true
            )

            val drama3 = DramaEntity(
                id = "drama_cinta_kontrak",
                title = "Cinta Kontrak Pewaris Takhta",
                genre = "Romantik Drama",
                synopsis = "Syarat wasiat 100 hari memaksa pewaris empayar hartanah mencari isteri segera. Kontrak ditandatangani dengan syarat ketat, namun perasaan cinta yang hadir di luar jangkaan mencabar setiap fasal perjanjian.",
                posterResName = "poster_ceo_gadis_1787165470282",
                totalEpisodes = 6,
                rating = 4.7,
                reviewsCount = 21,
                viewsCount = 195000,
                isFeatured = false
            )

            dramaDao.insertDramas(listOf(drama1, drama2, drama3))

            // Seed Episodes for Drama 1 (Ep 1-3 Free, Ep 4-10 Paid)
            val episodesDrama1 = listOf(
                EpisodeEntity("ep_ceo_01", drama1.id, 1, "Pertemuan Di Kedai Bunga", "sample_url_1", 90, isFree = true, coinPrice = 0, likesCount = 5420, commentsCount = 142, teaserText = "Tengku Zaril terserempak dengan Maya ketika keretanya rosak."),
                EpisodeEntity("ep_ceo_02", drama1.id, 2, "Perjanjian Rahsia RM50,000", "sample_url_2", 85, isFree = true, coinPrice = 0, likesCount = 4820, commentsCount = 98, teaserText = "Tawaran lumayan untuk menjadi tunang sementara."),
                EpisodeEntity("ep_ceo_03", drama1.id, 3, "Majlis Jamuan Mewah & Hinaan", "sample_url_3", 95, isFree = true, coinPrice = 0, likesCount = 6120, commentsCount = 210, teaserText = "Maya dihina oleh Datin Rozita di hadapan semua tetamu VIP."),
                EpisodeEntity("ep_ceo_04", drama1.id, 4, "Topeng CEO Terlucut", "sample_url_4", 92, isFree = false, coinPrice = 15, likesCount = 7400, commentsCount = 350, teaserText = "Tengku Zaril mempertahankan Maya dengan tegas di hadapan media!"),
                EpisodeEntity("ep_ceo_05", drama1.id, 5, "Pengkhianatan Bekas Tunang", "sample_url_5", 90, isFree = false, coinPrice = 15, likesCount = 6900, commentsCount = 190, teaserText = "Muslihat jahat Clarissa cuba memfitnah Maya mencuri rantai berlian."),
                EpisodeEntity("ep_ceo_06", drama1.id, 6, "Selamatkan Bidadari Desa", "sample_url_6", 88, isFree = false, coinPrice = 15, likesCount = 8200, commentsCount = 412, teaserText = "Zaril sanggup meredah ribut salji mencari Maya yang hilang."),
                EpisodeEntity("ep_ceo_07", drama1.id, 7, "Kebenaran Wasiat Arwah", "sample_url_7", 95, isFree = false, coinPrice = 20, likesCount = 9100, commentsCount = 520, teaserText = "Rahsia sebenar mengapa Zaril memilih Maya terbongkar."),
                EpisodeEntity("ep_ceo_08", drama1.id, 8, "Pertarungan Dewan Lembaga", "sample_url_8", 90, isFree = false, coinPrice = 20, likesCount = 8800, commentsCount = 380, teaserText = "Serangan musuh korporat dipatahkan dengan bukti rakaman audio."),
                EpisodeEntity("ep_ceo_09", drama1.id, 9, "Pengakuan Cinta Di Bawah Hujan", "sample_url_9", 100, isFree = false, coinPrice = 20, likesCount = 12400, commentsCount = 890, teaserText = "'Maya, kontrak itu batal. Aku cintakan kau dengan ikhlas!'"),
                EpisodeEntity("ep_ceo_10", drama1.id, 10, "Kebahagiaan Hakiki (Episod Akhir)", "sample_url_10", 110, isFree = false, coinPrice = 25, likesCount = 15900, commentsCount = 1200, teaserText = "Pernikahan sebenar yang gilang gemilang di atas kapal persiaran mewah.")
            )

            // Seed Episodes for Drama 2
            val episodesDrama2 = listOf(
                EpisodeEntity("ep_jutawan_01", drama2.id, 1, "Diusir Dari Rumah Agam", "sample_url_j1", 85, isFree = true, coinPrice = 0, likesCount = 3800, commentsCount = 90, teaserText = "Dani diusir dalam hujan lebat tanpa seurat benang harta."),
                EpisodeEntity("ep_jutawan_02", drama2.id, 2, "5 Tahun Di Lembah Kelam", "sample_url_j2", 90, isFree = true, coinPrice = 0, likesCount = 4200, commentsCount = 110, teaserText = "Membina empayar kewangan rahsia di pasaran saham global."),
                EpisodeEntity("ep_jutawan_03", drama2.id, 3, "Kembalinya Pewaris Terbuang", "sample_url_j3", 95, isFree = true, coinPrice = 0, likesCount = 5100, commentsCount = 180, teaserText = "Dani hadir ke lelongan tanah dengan dana tanpa had."),
                EpisodeEntity("ep_jutawan_04", drama2.id, 4, "Tamparan Pertama Korporat", "sample_url_j4", 88, isFree = false, coinPrice = 15, likesCount = 6300, commentsCount = 240, teaserText = "Membeli hutang musuh dan menuntut bayaran serta merta!"),
                EpisodeEntity("ep_jutawan_05", drama2.id, 5, "Beli Syarikat Musuh Tunai", "sample_url_j5", 92, isFree = false, coinPrice = 20, likesCount = 7800, commentsCount = 310, teaserText = "Cek bernilai RM100 Juta diserahkan di atas meja mesyuarat."),
                EpisodeEntity("ep_jutawan_06", drama2.id, 6, "Identiti Sebenar Terbongkar", "sample_url_j6", 90, isFree = false, coinPrice = 20, likesCount = 8900, commentsCount = 450, teaserText = "Semua tergamam mengetahui pemilik Dragon Capital adalah Dani!"),
                EpisodeEntity("ep_jutawan_07", drama2.id, 7, "Pukulan Pada Hari Pernikahan", "sample_url_j7", 95, isFree = false, coinPrice = 25, likesCount = 11200, commentsCount = 670, teaserText = "Video bukti kejahatan disiarkan di skrin gergasi majlis kahwin."),
                EpisodeEntity("ep_jutawan_08", drama2.id, 8, "Takhta Dituntut Semula", "sample_url_j8", 105, isFree = false, coinPrice = 25, likesCount = 14500, commentsCount = 980, teaserText = "Keadilan ditegakkan dan nama arwah bapa dibersihkan.")
            )

            episodeDao.insertEpisodes(episodesDrama1 + episodesDrama2)

            // Seed sample ratings & reviews
            reviewDao.insertReview(
                ReviewEntity(
                    id = "rev_1",
                    dramaId = drama1.id,
                    userId = "user_reviewer_01",
                    username = "Farah Nabilah",
                    rating = 5,
                    reviewText = "Plot cerita sangat mendebarkan! Lakonan watak Tengku Zaril sangat berkarisma. Tak sabar nak tonton season 2!",
                    createdAt = System.currentTimeMillis() - 86400000
                )
            )
            reviewDao.insertReview(
                ReviewEntity(
                    id = "rev_2",
                    dramaId = drama1.id,
                    userId = "user_reviewer_02",
                    username = "Hafiz Shah",
                    rating = 5,
                    reviewText = "Sangat berbaloi unlock episod berbayar. Kualiti video dan skrip memang taraf antarabangsa. 5 bintang!",
                    createdAt = System.currentTimeMillis() - 43200000
                )
            )
            reviewDao.insertReview(
                ReviewEntity(
                    id = "rev_3",
                    dramaId = drama2.id,
                    userId = "user_reviewer_03",
                    username = "Kamal Effendi",
                    rating = 5,
                    reviewText = "Aksi dan dendam Dani Mikail sangat memuaskan hati. Rasa macam tengok filem Hollywood pendek!",
                    createdAt = System.currentTimeMillis() - 25000000
                )
            )

            // Seed sample comments
            commentDao.insertComment(CommentEntity("c1", "ep_ceo_01", "Aina Syahirah", "", "Geram betul tengok Datin Rozita tu! Tak sabar nak tengok episod seterusnya! 🔥", System.currentTimeMillis() - 3600000, 24))
            commentDao.insertComment(CommentEntity("c2", "ep_ceo_01", "Farhan KL", "", "Lakonan Tengku Zaril mantap gila, aura CEO mahal sangat.", System.currentTimeMillis() - 7200000, 18))
            commentDao.insertComment(CommentEntity("c3", "ep_ceo_04", "Nurul Huda", "", "Puas hati Zaril tolong Maya! Berbaloi unlock episod ni guna syiling 😍", System.currentTimeMillis() - 1800000, 42))

            // Seed sample claimed voucher
            voucherDao.insertVoucher(
                ClaimedVoucherEntity(
                    id = "v_starter_01",
                    userId = "user_default_01",
                    voucherTitle = "Baucar Diskaun RM10 Nasadef Shop",
                    voucherCode = "NASADEF-RM10-WELCOME",
                    discountDescription = "Potongan RM10 untuk semua barangan di Kedai Rasmi Nasadef.",
                    pointsCost = 100,
                    shopUrl = "https://nasadef-website.web.app/",
                    claimedAt = System.currentTimeMillis() - 86400000,
                    isUsed = false
                )
            )

            // Seed sample topup transaction
            transactionDao.insertTransaction(
                TopupTransactionEntity(
                    id = "tx_sample_01",
                    userId = "user_default_01",
                    packageName = "Pakej Pemula (100 Syiling)",
                    amountMyr = 10.0,
                    coinsReward = 100,
                    bonusCoins = 10,
                    paymentMethod = "Touch 'n Go (DuitNow)",
                    accountNumber = "170997196734",
                    referenceNumber = "TNG-982736412",
                    receiptNote = "Pindahan DuitNow berjaya RM10 pada jam 2:30 PM",
                    status = "MENUNGGU_PENGESAHAN",
                    createdAt = System.currentTimeMillis() - 1500000
                )
            )
        }
    }

    suspend fun unlockEpisode(userId: String = "user_default_01", dramaId: String, episodeId: String, coinPrice: Int): Boolean = withContext(Dispatchers.IO) {
        val user = userDao.getUserSync(userId) ?: return@withContext false
        if (user.coinBalance < coinPrice) {
            return@withContext false
        }
        // Deduct coins, award 10 loyalty points for unlocking & record unlock
        userDao.deductCoins(userId, coinPrice)
        userDao.addRewardPoints(userId, 10)
        unlockedDao.insertUnlock(
            UnlockedEpisodeEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                dramaId = dramaId,
                episodeId = episodeId,
                coinsSpent = coinPrice,
                unlockedAt = System.currentTimeMillis()
            )
        )
        return@withContext true
    }

    suspend fun submitDramaReview(
        dramaId: String,
        rating: Int,
        reviewText: String,
        userId: String = "user_default_01",
        username: String = "Razif Penonton VIP"
    ): ReviewEntity = withContext(Dispatchers.IO) {
        val review = ReviewEntity(
            id = "rev_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}",
            dramaId = dramaId,
            userId = userId,
            username = username,
            rating = rating.coerceIn(1, 5),
            reviewText = reviewText.trim(),
            createdAt = System.currentTimeMillis()
        )
        reviewDao.insertReview(review)

        // Award 15 Reward points to user for writing a helpful review!
        userDao.addRewardPoints(userId, 15)

        // Recalculate average rating for drama
        val avgRating = reviewDao.getAverageRatingForDrama(dramaId) ?: rating.toDouble()
        val reviewCount = reviewDao.getReviewCountForDrama(dramaId)
        val formattedRating = Math.round(avgRating * 10.0) / 10.0
        dramaDao.updateRating(dramaId, formattedRating, reviewCount)

        review
    }

    suspend fun claimShopVoucher(
        reward: ShopVoucherReward,
        userId: String = "user_default_01"
    ): Result<ClaimedVoucherEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserSync(userId) ?: return@withContext Result.failure(Exception("Pengguna tidak dijumpai"))
        if (user.rewardPoints < reward.pointsRequired) {
            return@withContext Result.failure(Exception("Mata ganjaran tidak mencukupi. Anda perlukan ${reward.pointsRequired} mata."))
        }

        userDao.deductRewardPoints(userId, reward.pointsRequired)

        val uniqueSuffix = (1000..9999).random().toString()
        val code = "${reward.codePrefix}-$uniqueSuffix"

        val voucher = ClaimedVoucherEntity(
            id = "v_${System.currentTimeMillis()}",
            userId = userId,
            voucherTitle = reward.title,
            voucherCode = code,
            discountDescription = reward.discountDesc,
            pointsCost = reward.pointsRequired,
            shopUrl = "https://nasadef-website.web.app/",
            claimedAt = System.currentTimeMillis(),
            isUsed = false
        )
        voucherDao.insertVoucher(voucher)
        Result.success(voucher)
    }

    suspend fun watchRewardedAd(
        ad: SponsoredAd,
        userId: String = "user_default_01"
    ): Pair<Int, Int> = withContext(Dispatchers.IO) {
        userDao.recordAdWatchedReward(userId, ad.rewardCoins, ad.rewardPoints)
        Pair(ad.rewardCoins, ad.rewardPoints)
    }

    suspend fun submitTopupReceipt(
        userId: String = "user_default_01",
        coinPackage: CoinPackage,
        paymentMethod: String,
        accountNumber: String,
        referenceNumber: String,
        receiptNote: String
    ): TopupTransactionEntity = withContext(Dispatchers.IO) {
        val tx = TopupTransactionEntity(
            id = "tx_${System.currentTimeMillis()}",
            userId = userId,
            packageName = "${coinPackage.name} (${coinPackage.coins} Syiling)",
            amountMyr = coinPackage.amountMyr,
            coinsReward = coinPackage.coins,
            bonusCoins = coinPackage.bonusCoins,
            paymentMethod = paymentMethod,
            accountNumber = accountNumber,
            referenceNumber = referenceNumber,
            receiptNote = receiptNote,
            status = "MENUNGGU_PENGESAHAN",
            createdAt = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(tx)
        tx
    }

    suspend fun approveTopupTransaction(transaction: TopupTransactionEntity) = withContext(Dispatchers.IO) {
        val totalCoins = transaction.coinsReward + transaction.bonusCoins
        userDao.addCoins(transaction.userId, totalCoins)
        // Also award points for topup loyalty
        userDao.addRewardPoints(transaction.userId, (transaction.amountMyr * 10).toInt())
        transactionDao.updateTransactionStatus(transaction.id, "BERJAYA", System.currentTimeMillis())
    }

    suspend fun rejectTopupTransaction(transactionId: String) = withContext(Dispatchers.IO) {
        transactionDao.updateTransactionStatus(transactionId, "DITOLAK", System.currentTimeMillis())
    }

    suspend fun toggleLike(userId: String = "user_default_01", episodeId: String, currentlyLiked: Boolean) = withContext(Dispatchers.IO) {
        if (currentlyLiked) {
            likeDao.deleteLike(userId, episodeId)
            episodeDao.updateLikeCount(episodeId, -1)
        } else {
            likeDao.insertLike(LikeEntity(UUID.randomUUID().toString(), userId, episodeId))
            episodeDao.updateLikeCount(episodeId, 1)
        }
    }

    suspend fun addComment(episodeId: String, username: String, content: String) = withContext(Dispatchers.IO) {
        commentDao.insertComment(
            CommentEntity(
                id = UUID.randomUUID().toString(),
                episodeId = episodeId,
                username = username,
                content = content,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addNewDrama(
        title: String,
        genre: String,
        synopsis: String,
        totalEpisodes: Int,
        freeEpisodesCount: Int,
        defaultCoinPrice: Int
    ) = withContext(Dispatchers.IO) {
        val dramaId = "drama_${System.currentTimeMillis()}"
        val newDrama = DramaEntity(
            id = dramaId,
            title = title,
            genre = genre,
            synopsis = synopsis,
            posterResName = "poster_ceo_gadis_1787165470282",
            totalEpisodes = totalEpisodes,
            rating = 5.0,
            reviewsCount = 1,
            viewsCount = 120,
            isFeatured = true,
            createdAt = System.currentTimeMillis()
        )
        dramaDao.insertDrama(newDrama)

        val episodeList = mutableListOf<EpisodeEntity>()
        for (i in 1..totalEpisodes) {
            val isFree = i <= freeEpisodesCount
            episodeList.add(
                EpisodeEntity(
                    id = "ep_${dramaId}_$i",
                    dramaId = dramaId,
                    episodeNumber = i,
                    title = "Episod $i: Kemuncak Konflik",
                    videoUrl = "sample_url_$i",
                    durationSeconds = 90,
                    isFree = isFree,
                    coinPrice = if (isFree) 0 else defaultCoinPrice,
                    likesCount = (10..50).random(),
                    commentsCount = (2..10).random(),
                    teaserText = "Ketegangan semakin memuncak dalam episod ke-$i."
                )
            )
        }
        episodeDao.insertEpisodes(episodeList)
    }

    suspend fun deleteDrama(dramaId: String) = withContext(Dispatchers.IO) {
        dramaDao.deleteDrama(dramaId)
    }
}
