package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ClaimedVoucherEntity
import com.example.data.model.CoinPackage
import com.example.data.model.CommentEntity
import com.example.data.model.DramaEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.PaymentChannel
import com.example.data.model.ReviewEntity
import com.example.data.model.ShopVoucherReward
import com.example.data.model.SponsoredAd
import com.example.data.model.TopupTransactionEntity
import com.example.data.model.UserEntity
import com.example.data.repository.DramaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DramaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = DramaRepository(db)

    companion object {
        const val TRADEMARK_TEXT = "RazifApps@Nasadef"
        const val TRADEMARK_URL = "https://nasadef.com.my"
        const val SHOP_URL = "https://nasadef-website.web.app/"
        const val DRIVE_AD_VIDEO_URL = "https://drive.google.com/file/d/1KTWIYxRnCbmhFE_sQoGutMMVFWhrnM6s/view?usp=drive_link"
        const val YOUTUBE_AD_VIDEO_URL = "https://youtu.be/EvwdsI9G6-o"
    }

    // User State
    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dramas
    val dramas: StateFlow<List<DramaEntity>> = repository.allDramas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Selected Drama
    private val _selectedDrama = MutableStateFlow<DramaEntity?>(null)
    val selectedDrama: StateFlow<DramaEntity?> = _selectedDrama.asStateFlow()

    // Current Episode Index in the active drama
    private val _currentEpisodeIndex = MutableStateFlow(0)
    val currentEpisodeIndex: StateFlow<Int> = _currentEpisodeIndex.asStateFlow()

    // Episodes for current drama
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentEpisodes: StateFlow<List<EpisodeEntity>> = _selectedDrama
        .flatMapLatest { drama ->
            if (drama != null) repository.getEpisodesForDrama(drama.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unlocked Episode IDs for user
    val unlockedEpisodeIds: StateFlow<Set<String>> = repository.getUnlockedEpisodes("user_default_01")
        .map { list -> list.map { it.episodeId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Transactions
    val allTransactions: StateFlow<List<TopupTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTransactions: StateFlow<List<TopupTransactionEntity>> = repository.pendingTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Claimed Shop Vouchers
    val userVouchers: StateFlow<List<ClaimedVoucherEntity>> = repository.userVouchers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Auto-unlock next episode preference
    private val _autoUnlockNext = MutableStateFlow(true)
    val autoUnlockNext: StateFlow<Boolean> = _autoUnlockNext.asStateFlow()

    // Playback state
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // Admin Mode toggle
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Dialog and Sheet States
    private val _isEpisodeSelectorOpen = MutableStateFlow(false)
    val isEpisodeSelectorOpen: StateFlow<Boolean> = _isEpisodeSelectorOpen.asStateFlow()

    private val _isCommentsOpen = MutableStateFlow(false)
    val isCommentsOpen: StateFlow<Boolean> = _isCommentsOpen.asStateFlow()

    private val _isReviewsSheetOpen = MutableStateFlow(false)
    val isReviewsSheetOpen: StateFlow<Boolean> = _isReviewsSheetOpen.asStateFlow()

    private val _isTopupDialogOpen = MutableStateFlow(false)
    val isTopupDialogOpen: StateFlow<Boolean> = _isTopupDialogOpen.asStateFlow()

    private val _isShopVoucherDialogOpen = MutableStateFlow(false)
    val isShopVoucherDialogOpen: StateFlow<Boolean> = _isShopVoucherDialogOpen.asStateFlow()

    // Advertising & Reward Ad State
    private val _isAdDialogOpen = MutableStateFlow(false)
    val isAdDialogOpen: StateFlow<Boolean> = _isAdDialogOpen.asStateFlow()

    private val _activeAd = MutableStateFlow<SponsoredAd?>(null)
    val activeAd: StateFlow<SponsoredAd?> = _activeAd.asStateFlow()

    // Notification toast events
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Available Coin Packages
    val coinPackages = listOf(
        CoinPackage("pkg_10", "Pakej Pemula", 10.0, 100, 10, isPopular = false, tag = "Jimat"),
        CoinPackage("pkg_25", "Pakej Popular", 25.0, 280, 50, isPopular = true, tag = "Paling Laris 🔥"),
        CoinPackage("pkg_50", "Pakej Nilai Hebat", 50.0, 600, 150, isPopular = false, tag = "Ekstra 25%"),
        CoinPackage("pkg_100", "Pakej VIP Jutawan", 100.0, 1400, 400, isPopular = false, tag = "Paling Jimat 💎")
    )

    // Available Shop Voucher Rewards (Claimable with accumulated points)
    val availableShopVouchers = listOf(
        ShopVoucherReward(
            id = "v_rm10",
            title = "Baucar Diskaun RM10",
            discountDesc = "Potongan tunai RM10 tanpa had perbelanjaan minimum di Nasadef Shop.",
            pointsRequired = 100,
            codePrefix = "NASADEF-RM10",
            badge = "Paling Popular 🔥"
        ),
        ShopVoucherReward(
            id = "v_20off",
            title = "Diskaun 20% Pembelian",
            discountDesc = "Potongan 20% bagi sebarang produk terpilih di Kedai Web Nasadef.",
            pointsRequired = 180,
            codePrefix = "NASADEF-20OFF",
            badge = "Nilai Hebat"
        ),
        ShopVoucherReward(
            id = "v_rm25",
            title = "Baucar Diskaun RM25 VIP",
            discountDesc = "Potongan RM25 untuk pembelian RM50 ke atas di portal Nasadef.",
            pointsRequired = 250,
            codePrefix = "NASADEF-RM25",
            badge = "Eksklusif VIP 💎"
        ),
        ShopVoucherReward(
            id = "v_freeship",
            title = "Kupon Penghantaran Percuma",
            discountDesc = "Percuma pos ke seluruh Semenanjung & Sabah/Sarawak di Nasadef Shop.",
            pointsRequired = 50,
            codePrefix = "NASADEF-FREESHIP",
            badge = "Mudah Tebus"
        )
    )

    // Sample Advertisements (Google Ads / Sponsored Monetization with Requested Video Links)
    val sampleAds = listOf(
        SponsoredAd(
            id = "ad_drive_nasadef",
            title = "Iklan Video Google Drive: Koleksi Eksklusif Nasadef",
            sponsorName = "Nasadef Malaysia (Iklan Video Google Drive)",
            description = "Tonton video tajaan promosi rasmi dari Google Drive untuk mendapat ganjaran syiling dan baucar diskaun di kedai!",
            actionText = "Tonton Video Google Drive",
            targetUrl = SHOP_URL,
            videoUrl = DRIVE_AD_VIDEO_URL,
            videoType = "DRIVE",
            rewardCoins = 15,
            rewardPoints = 60,
            durationSeconds = 10
        ),
        SponsoredAd(
            id = "ad_youtube_drama",
            title = "Iklan Video YouTube: DramaShort Rasmi (EvwdsI9G6-o)",
            sponsorName = "YouTube Ads Tajaan Rasmi",
            description = "Saksikan video promosi YouTube eksklusif dan kumpulkan syiling percuma untuk membuka episod drama pendek!",
            actionText = "Tonton Video di YouTube",
            targetUrl = SHOP_URL,
            videoUrl = YOUTUBE_AD_VIDEO_URL,
            videoType = "YOUTUBE",
            rewardCoins = 15,
            rewardPoints = 60,
            durationSeconds = 10
        ),
        SponsoredAd(
            id = "ad_nasadef_trademark",
            title = "Langganan VIP & Kedai Rasmi RazifApps@Nasadef",
            sponsorName = "RazifApps@Nasadef Google Ads",
            description = "Dapatkan produk berkualiti, diskaun hebat, dan akses tanpa had drama alih suara Melayu.",
            actionText = "Lawati Kedai Nasadef",
            targetUrl = SHOP_URL,
            videoUrl = YOUTUBE_AD_VIDEO_URL,
            videoType = "YOUTUBE",
            rewardCoins = 10,
            rewardPoints = 50,
            durationSeconds = 8
        )
    )

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
        viewModelScope.launch {
            dramas.collect { list ->
                if (_selectedDrama.value == null && list.isNotEmpty()) {
                    _selectedDrama.value = list.first()
                }
            }
        }
    }

    fun selectDrama(drama: DramaEntity, startEpisodeIndex: Int = 0) {
        _selectedDrama.value = drama
        _currentEpisodeIndex.value = startEpisodeIndex
        _isPlaying.value = true
    }

    fun setEpisodeIndex(index: Int) {
        val episodes = currentEpisodes.value
        if (index in episodes.indices) {
            _currentEpisodeIndex.value = index
            _isPlaying.value = true
            checkAndAutoUnlockIfNeeded(episodes[index])
        }
    }

    fun nextEpisode() {
        val episodes = currentEpisodes.value
        val nextIdx = _currentEpisodeIndex.value + 1
        if (nextIdx < episodes.size) {
            setEpisodeIndex(nextIdx)
        } else {
            viewModelScope.launch {
                _toastMessage.emit("Anda telah tiba di episod terakhir drama ini!")
            }
        }
    }

    fun previousEpisode() {
        val prevIdx = _currentEpisodeIndex.value - 1
        if (prevIdx >= 0) {
            setEpisodeIndex(prevIdx)
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun toggleAutoUnlock() {
        _autoUnlockNext.value = !_autoUnlockNext.value
        viewModelScope.launch {
            val status = if (_autoUnlockNext.value) "diaktifkan" else "dimatikan"
            _toastMessage.emit("Buka episod automatik telah $status.")
        }
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
        viewModelScope.launch {
            val text = if (_isAdminMode.value) "Mod Admin diaktifkan" else "Kembali ke mod Pengguna"
            _toastMessage.emit(text)
        }
    }

    fun setEpisodeSelectorOpen(open: Boolean) {
        _isEpisodeSelectorOpen.value = open
    }

    fun setCommentsOpen(open: Boolean) {
        _isCommentsOpen.value = open
    }

    fun setReviewsSheetOpen(open: Boolean) {
        _isReviewsSheetOpen.value = open
    }

    fun setTopupDialogOpen(open: Boolean) {
        _isTopupDialogOpen.value = open
    }

    fun setShopVoucherDialogOpen(open: Boolean) {
        _isShopVoucherDialogOpen.value = open
    }

    // Rating and Review functions
    fun getReviewsForDrama(dramaId: String): Flow<List<ReviewEntity>> =
        repository.getReviewsForDrama(dramaId)

    fun submitReview(dramaId: String, rating: Int, reviewText: String) {
        viewModelScope.launch {
            if (reviewText.isBlank()) {
                _toastMessage.emit("Sila tulis sedikit ulasan drama ini.")
                return@launch
            }
            val user = currentUser.value
            val username = user?.username ?: "Penonton"
            repository.submitDramaReview(dramaId, rating, reviewText, username = username)
            _toastMessage.emit("Terima kasih! Ulasan anda ($rating Bintang) telah dihantar (+15 Mata Ganjaran Shop).")
            _isReviewsSheetOpen.value = false
        }
    }

    // Voucher Redemption function
    fun claimShopVoucher(reward: ShopVoucherReward) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user == null || user.rewardPoints < reward.pointsRequired) {
                _toastMessage.emit("Mata ganjaran anda (${user?.rewardPoints ?: 0}) tidak mencukupi untuk tebus baucar ini.")
                return@launch
            }
            val result = repository.claimShopVoucher(reward)
            if (result.isSuccess) {
                val voucher = result.getOrNull()
                _toastMessage.emit("Tahniah! Baucar '${voucher?.voucherCode}' berjaya ditebus untuk Nasadef Shop.")
            } else {
                _toastMessage.emit(result.exceptionOrNull()?.message ?: "Gagal menebus baucar.")
            }
        }
    }

    // Rewarded Ads Functions
    fun showRewardedAd(customAd: SponsoredAd? = null) {
        _activeAd.value = customAd ?: sampleAds.random()
        _isAdDialogOpen.value = true
    }

    fun dismissAd() {
        _isAdDialogOpen.value = false
        _activeAd.value = null
    }

    fun completeRewardedAd(ad: SponsoredAd) {
        viewModelScope.launch {
            val (coins, points) = repository.watchRewardedAd(ad)
            _isAdDialogOpen.value = false
            _activeAd.value = null
            _toastMessage.emit("🎉 Hebat! Anda menerima +$coins Syiling & +$points Mata Ganjaran Shop!")
        }
    }

    private fun checkAndAutoUnlockIfNeeded(episode: EpisodeEntity) {
        if (!episode.isFree && _autoUnlockNext.value) {
            val unlocked = unlockedEpisodeIds.value.contains(episode.id)
            if (!unlocked) {
                unlockCurrentEpisode(episode, isSilent = true)
            }
        }
    }

    fun unlockCurrentEpisode(episode: EpisodeEntity, isSilent: Boolean = false) {
        viewModelScope.launch {
            val drama = _selectedDrama.value ?: return@launch
            val success = repository.unlockEpisode(
                dramaId = drama.id,
                episodeId = episode.id,
                coinPrice = episode.coinPrice
            )
            if (success) {
                _isPlaying.value = true
                if (!isSilent) {
                    _toastMessage.emit("Episod ${episode.episodeNumber} berjaya dibuka (-${episode.coinPrice} Syiling, +10 Mata Shop)!")
                }
            } else {
                if (!isSilent) {
                    _toastMessage.emit("Baki syiling tidak mencukupi (${currentUser.value?.coinBalance ?: 0} Syiling). Sila tambah nilai.")
                    _isTopupDialogOpen.value = true
                }
            }
        }
    }

    fun submitTopupReceipt(
        coinPackage: CoinPackage,
        paymentChannel: PaymentChannel,
        referenceNumber: String,
        receiptNote: String
    ) {
        viewModelScope.launch {
            if (referenceNumber.isBlank()) {
                _toastMessage.emit("Sila masukkan No. Rujukan transaksi perbankan / DuitNow anda.")
                return@launch
            }
            repository.submitTopupReceipt(
                coinPackage = coinPackage,
                paymentMethod = paymentChannel.channelName,
                accountNumber = paymentChannel.accountNumber,
                referenceNumber = referenceNumber,
                receiptNote = receiptNote
            )
            _isTopupDialogOpen.value = false
            _toastMessage.emit("Bukti pembayaran berjaya dihantar! Admin akan mengesahkan baki anda tidak lama lagi.")
        }
    }

    fun approveTransaction(transaction: TopupTransactionEntity) {
        viewModelScope.launch {
            repository.approveTopupTransaction(transaction)
            _toastMessage.emit("Transaksi ${transaction.referenceNumber} berjaya diluluskan (+${transaction.coinsReward + transaction.bonusCoins} Syiling).")
        }
    }

    fun rejectTransaction(transactionId: String) {
        viewModelScope.launch {
            repository.rejectTopupTransaction(transactionId)
            _toastMessage.emit("Transaksi telah ditolak.")
        }
    }

    fun toggleLike(episode: EpisodeEntity, currentlyLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleLike(episodeId = episode.id, currentlyLiked = currentlyLiked)
        }
    }

    fun isLiked(episodeId: String): Flow<Boolean> = repository.isLiked(episodeId = episodeId)

    fun getCommentsForEpisode(episodeId: String): Flow<List<CommentEntity>> =
        repository.getCommentsForEpisode(episodeId)

    fun addComment(episodeId: String, text: String) {
        viewModelScope.launch {
            if (text.isBlank()) return@launch
            val username = currentUser.value?.username ?: "Penonton"
            repository.addComment(episodeId, username, text.trim())
            _toastMessage.emit("Komen anda telah dihantar!")
        }
    }

    fun addNewDrama(
        title: String,
        genre: String,
        synopsis: String,
        totalEpisodes: Int,
        freeEpisodesCount: Int,
        defaultCoinPrice: Int
    ) {
        createNewDrama(title, genre, synopsis, totalEpisodes, freeEpisodesCount, defaultCoinPrice)
    }

    fun createNewDrama(
        title: String,
        genre: String,
        synopsis: String,
        totalEpisodes: Int,
        freeEpisodesCount: Int,
        defaultCoinPrice: Int
    ) {
        viewModelScope.launch {
            if (title.isBlank() || synopsis.isBlank()) {
                _toastMessage.emit("Sila isi tajuk dan sinopsis drama.")
                return@launch
            }
            repository.addNewDrama(title, genre, synopsis, totalEpisodes, freeEpisodesCount, defaultCoinPrice)
            _toastMessage.emit("Drama '$title' berjaya dimuat naik ke platform!")
        }
    }

    fun deleteDrama(dramaId: String) {
        viewModelScope.launch {
            repository.deleteDrama(dramaId)
            _toastMessage.emit("Drama telah dipadamkan.")
        }
    }
}
