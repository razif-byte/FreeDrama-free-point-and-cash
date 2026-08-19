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
                    coinBalance = 50, // Starter free coins
                    rewardPoints = 200, // Starter loyalty reward points for shop
                    isVip = false,
                    totalEpisodesWatched = 8,
                    totalAdsWatched = 4
                )
            )
        }

        seed127MalayDubbedCatalog()
    }

    private suspend fun seed127MalayDubbedCatalog() {
        val driveVideoUrl = "https://drive.google.com/file/d/1KTWIYxRnCbmhFE_sQoGutMMVFWhrnM6s/view?usp=drive_link"
        val youtubeVideoUrl = "https://youtu.be/EvwdsI9G6-o"

        // 7 Curated Short Drama Series with Full Malay Dubbing (Total = 127 Episodes)
        val dramas = listOf(
            DramaEntity(
                id = "drama_ceo_gadis",
                title = "CEO Terpikat Gadis Kampung (Dubbing Melayu 🇲🇾)",
                genre = "Romantik Korporat • Alih Suara Melayu",
                synopsis = "Gadis desa polos berhijrah ke KL demi rawatan ibunya. Pertemuan tak sengaja dengan Tengku Zaril, CEO jutawan sombong, mencetuskan perkahwinan kontrak yang penuh rasa cinta mendalam. Alih suara Bahasa Melayu penuh bertaraf profesional.",
                posterResName = "poster_ceo_gadis_1787165470282",
                totalEpisodes = 20,
                rating = 4.9,
                reviewsCount = 68,
                viewsCount = 542100,
                isFeatured = true
            ),
            DramaEntity(
                id = "drama_jutawan_rahsia",
                title = "Dendam Sang Jutawan Tersembunyi (Dubbing Melayu 🇲🇾)",
                genre = "Dendam & Aksi • Alih Suara Melayu",
                synopsis = "Dani Mikail dianiaya dan diusir dari keluarga konglomerat. Selepas 5 tahun menguasai sindiket kewangan global, dia pulang menuntut bela dan membongkar muslihat keluarga tirinya. Audio alih suara Melayu asli.",
                posterResName = "poster_jutawan_rahsia_1787165486763",
                totalEpisodes = 20,
                rating = 4.8,
                reviewsCount = 52,
                viewsCount = 489400,
                isFeatured = true
            ),
            DramaEntity(
                id = "drama_cinta_kontrak",
                title = "Cinta Kontrak Pewaris Takhta (Dubbing Melayu 🇲🇾)",
                genre = "Cinta Kontrak • Alih Suara Melayu",
                synopsis = "Wasiat 100 hari memaksa pewaris empayar hartanah berkahwin segera. Gadis biasa dipilih menandatangani perjanjian sulit, namun perasaan cinta yang tumbuh merungkai segala rahsia keluarga diraja.",
                posterResName = "poster_ceo_gadis_1787165470282",
                totalEpisodes = 20,
                rating = 4.8,
                reviewsCount = 44,
                viewsCount = 395000,
                isFeatured = false
            ),
            DramaEntity(
                id = "drama_permaisuri_naga",
                title = "Kembalinya Sang Permaisuri Naga (Dubbing Melayu 🇲🇾)",
                genre = "Fantasi & Kuasa • Alih Suara Melayu",
                synopsis = "Dikhianati oleh adik kandung dan suami sendiri, Permaisuri Naga bangkit dengan kuasa sakti purba untuk menghukum mereka yang zalim dan mengambil kembali mahkota kerajaannya.",
                posterResName = "poster_jutawan_rahsia_1787165486763",
                totalEpisodes = 20,
                rating = 4.9,
                reviewsCount = 76,
                viewsCount = 612000,
                isFeatured = true
            ),
            DramaEntity(
                id = "drama_rider_bilionair",
                title = "Bilionair Menyamar Jadi Rider (Dubbing Melayu 🇲🇾)",
                genre = "Komedi & Korporat • Alih Suara Melayu",
                synopsis = "Tengku Iskandar menyamar sebagai rider penghantar makanan untuk mencari jodoh ikhlas tanpa memandang harta. Namun, keluarga teman wanitanya kerap menghinanya sehingga kebenaran sebenar terdedah.",
                posterResName = "poster_ceo_gadis_1787165470282",
                totalEpisodes = 20,
                rating = 4.7,
                reviewsCount = 38,
                viewsCount = 420000,
                isFeatured = false
            ),
            DramaEntity(
                id = "drama_isteri_pengawal",
                title = "Rahsia Isteri Pengawal Peribadi (Dubbing Melayu 🇲🇾)",
                genre = "Aksi Romantik • Alih Suara Melayu",
                synopsis = "Di siang hari dia seorang pengawal peribadi wanita yang tegas, tetapi di malam hari dia adalah isteri rahsia kepada pengerusi syarikat keselamatan terbesar Asia Tenggara.",
                posterResName = "poster_jutawan_rahsia_1787165486763",
                totalEpisodes = 15,
                rating = 4.8,
                reviewsCount = 31,
                viewsCount = 310000,
                isFeatured = false
            ),
            DramaEntity(
                id = "drama_ibu_mertua",
                title = "Ibu Mertua Angkuh Berlutut (Dubbing Melayu 🇲🇾)",
                genre = "Drama Keluarga • Alih Suara Melayu",
                synopsis = "Menantu lelaki sering dicaci dan disuruh membuat kerja rumah oleh ibu mertua yang tamak. Hakikatnya, seluruh banglo mewah dan syarikat keluarga itu dibeli dengan dana rahsia menantu tersebut!",
                posterResName = "poster_ceo_gadis_1787165470282",
                totalEpisodes = 12,
                rating = 4.9,
                reviewsCount = 59,
                viewsCount = 580000,
                isFeatured = true
            )
        )

        dramaDao.insertDramas(dramas)

        // Generate exactly 127 Malay Dubbed video episodes (20 + 20 + 20 + 20 + 20 + 15 + 12 = 127)
        val all127Episodes = mutableListOf<EpisodeEntity>()

        // 1. Drama 1: CEO Terpikat Gadis Kampung (20 Episodes)
        val titles1 = listOf(
            "Pertemuan Di Kedai Bunga", "Perjanjian Rahsia RM50,000", "Majlis Jamuan Mewah & Hinaan",
            "Topeng CEO Terlucut", "Pengkhianatan Bekas Tunang", "Selamatkan Bidadari Desa",
            "Kebenaran Wasiat Arwah", "Pertarungan Dewan Lembaga", "Pengakuan Cinta Di Bawah Hujan",
            "Cemburu Buta Clarissa", "Penculikan Di Villa Mewah", "Zaril Mengamuk Selamatkan Maya",
            "Keluarga Tengku Merestui", "Musuh Rahsia Muncul Kembali", "Sumpah Setia Di Menara KL",
            "Pendedahan Di Sidang Media", "Detik Romantis Di Genting", "Surat Rahsia Ibu Maya",
            "Malam Sebelum Pernikahan", "Kebahagiaan Hakiki (Kemuncak Akhir)"
        )
        titles1.forEachIndexed { idx, epTitle ->
            val epNum = idx + 1
            val isFree = epNum <= 4
            val videoUrl = if (epNum % 2 == 0) youtubeVideoUrl else driveVideoUrl
            all127Episodes.add(
                EpisodeEntity(
                    id = "ep_ceo_${String.format("%02d", epNum)}",
                    dramaId = "drama_ceo_gadis",
                    episodeNumber = epNum,
                    title = epTitle,
                    videoUrl = videoUrl,
                    durationSeconds = 85 + (epNum * 2),
                    isFree = isFree,
                    coinPrice = if (isFree) 0 else 15,
                    likesCount = 5000 + (epNum * 420),
                    commentsCount = 120 + (epNum * 25),
                    teaserText = "[Dubbing Melayu 🇲🇾] Episod $epNum: $epTitle. Saksikan lakonan alih suara Melayu yang penuh emosi!"
                )
            )
        }

        // 2. Drama 2: Dendam Sang Jutawan Tersembunyi (20 Episodes)
        val titles2 = listOf(
            "Diusir Dari Rumah Agam", "5 Tahun Di Lembah Kelam", "Kembalinya Pewaris Terbuang",
            "Tamparan Pertama Korporat", "Beli Syarikat Musuh Tunai", "Identiti Sebenar Terbongkar",
            "Pukulan Pada Hari Pernikahan", "Takhta Dituntut Semula", "Pemberontakan Lembaga Pengarah",
            "Kebenaran Kemalangan Bapa", "Pertarungan Fizikal Di Gudang", "Dani Menyelamatkan Adik Kandung",
            "DuitNow RM50 Juta Ditolak", "Runtuhnya Empayar Musuh", "Pengakuan Dosa Ibu Tiri",
            "Penyatuan Semula Saham Keluarga", "Perangkap Terakhir Di Pulau", "Kemenangan Mutlak Dani",
            "Memaafkan Namun Tak Lupa", "Kedaulatan Dragon Capital (Episod Akhir)"
        )
        titles2.forEachIndexed { idx, epTitle ->
            val epNum = idx + 1
            val isFree = epNum <= 4
            val videoUrl = if (epNum % 2 == 0) driveVideoUrl else youtubeVideoUrl
            all127Episodes.add(
                EpisodeEntity(
                    id = "ep_jutawan_${String.format("%02d", epNum)}",
                    dramaId = "drama_jutawan_rahsia",
                    episodeNumber = epNum,
                    title = epTitle,
                    videoUrl = videoUrl,
                    durationSeconds = 80 + (epNum * 2),
                    isFree = isFree,
                    coinPrice = if (isFree) 0 else 15,
                    likesCount = 4200 + (epNum * 380),
                    commentsCount = 95 + (epNum * 20),
                    teaserText = "[Dubbing Melayu 🇲🇾] Episod $epNum: $epTitle. Suara alih bahasa mantap dendam membara!"
                )
            )
        }

        // 3. Drama 3: Cinta Kontrak Pewaris Takhta (20 Episodes)
        val titles3 = listOf(
            "Syarat Wasiat 100 Hari", "Tandatangan Kontrak Perkahwinan", "Malam Pertama Penuh Dingin",
            "Ujian Dari Nenek Moyang", "Cemburu Melihat Sahabat Lama", "Penyelamatan Di Kolam Renang",
            "Perjalanan Bulan Madu Palsu", "Hati Yang Mula Berbunga", "Rahsia Gelap Keluarga Diraja",
            "Gadis Biasa Berjiwa Mulia", "Fitnah Cincin Warisan Hilang", "Bukti Rakaman CCTV Ditemui",
            "Pewaris Mengakui Kasih Sayang", "Kontrak Dirobek Menjadi Debu", "Serangan Musuh Politik Istana",
            "Pengorbanan Demi Mahkota", "Pendedahan Asal Usul Maya", "Restu Seluruh Kerabat",
            "Istiadat Pengurniaan Gelaran", "Cinta Abadi Takhta Diraja (Episod Akhir)"
        )
        titles3.forEachIndexed { idx, epTitle ->
            val epNum = idx + 1
            val isFree = epNum <= 3
            val videoUrl = if (epNum % 2 == 0) youtubeVideoUrl else driveVideoUrl
            all127Episodes.add(
                EpisodeEntity(
                    id = "ep_kontrak_${String.format("%02d", epNum)}",
                    dramaId = "drama_cinta_kontrak",
                    episodeNumber = epNum,
                    title = epTitle,
                    videoUrl = videoUrl,
                    durationSeconds = 90 + (epNum * 2),
                    isFree = isFree,
                    coinPrice = if (isFree) 0 else 15,
                    likesCount = 3800 + (epNum * 310),
                    commentsCount = 80 + (epNum * 18),
                    teaserText = "[Dubbing Melayu 🇲🇾] Episod $epNum: $epTitle. Drama romantik istana alih suara Melayu."
                )
            )
        }

        // 4. Drama 4: Kembalinya Sang Permaisuri Naga (20 Episodes)
        val titles4 = listOf(
            "Pengkhianatan Di Altar Naga", "Kebangkitan Dari Abu Api", "Kuasa Sakti Purba Terbuka",
            "Kembali Menyamar Sebagai Pelayan", "Menampar Gundik Yang Curang", "Raja Tergamam Melihat Aura",
            "Pertempuran Di Hutan Terlarang", "Titisan Darah Bunga Teratai", "Pendedahan Muslihat Menteri Kanan",
            "Penyatuan Puak Naga Langit", "Hukuman Pertama Pengkhianat", "Pedang Naga Berkilauan",
            "Penebusan Dosa Panglima Setia", "Penyerbuan Istana Utama", "Raja Berlutut Meminta Ampun",
            "Kembalinya Mahkota Permaisuri", "Pemulihan Negeri Yang Damai", "Sumpah Permaisuri Melindungi Rakyat",
            "Pemberontakan Terakhir Dipatahkan", "Keagungan Permaisuri Naga Selamanya"
        )
        titles4.forEachIndexed { idx, epTitle ->
            val epNum = idx + 1
            val isFree = epNum <= 3
            val videoUrl = if (epNum % 2 == 0) driveVideoUrl else youtubeVideoUrl
            all127Episodes.add(
                EpisodeEntity(
                    id = "ep_naga_${String.format("%02d", epNum)}",
                    dramaId = "drama_permaisuri_naga",
                    episodeNumber = epNum,
                    title = epTitle,
                    videoUrl = videoUrl,
                    durationSeconds = 88 + (epNum * 2),
                    isFree = isFree,
                    coinPrice = if (isFree) 0 else 20,
                    likesCount = 6100 + (epNum * 490),
                    commentsCount = 150 + (epNum * 30),
                    teaserText = "[Dubbing Melayu 🇲🇾] Episod $epNum: $epTitle. Aksi fantasi alih suara Melayu memukau!"
                )
            )
        }

        // 5. Drama 5: Bilionair Menyamar Jadi Rider (20 Episodes)
        val titles5 = listOf(
            "Hantar Makanan Ke Wisma Megah", "Dihina Kerana Uniform Basah", "Pertemuan Gadis Berhati Emas",
            "Beli Restoran Mewah Serta Merta", "Majlis Hari Jadi Teman Lelaki Angkuh", "Kad Kredit Hitam Tanpa Had",
            "Menghalau Pengurus Biadap", "Melindungi Warung Mak Cik Sarah", "Kereta Ferrari Rider Di Parkir",
            "Musuh Menyewa Samseng Jalanan", "Iskandar Menunjukkan Seni Bela Diri", "Keluarga Gadis Terkejut Besar",
            "Cek Tajaan RM10 Juta", "Pendedahan Di Meja Mesyuarat Induk", "Kisah Sebenar Di Sebalik Samaran",
            "Lamaran Romantis Di Atas Skuter", "Pernikahan Gaya Rakyat Sederhana", "Bantuan Amal Ke Seluruh Negara",
            "Perang Korporat Terakhir Dimenangi", "Kebahagiaan Sang Bilionair Dermawan"
        )
        titles5.forEachIndexed { idx, epTitle ->
            val epNum = idx + 1
            val isFree = epNum <= 4
            val videoUrl = if (epNum % 2 == 0) youtubeVideoUrl else driveVideoUrl
            all127Episodes.add(
                EpisodeEntity(
                    id = "ep_rider_${String.format("%02d", epNum)}",
                    dramaId = "drama_rider_bilionair",
                    episodeNumber = epNum,
                    title = epTitle,
                    videoUrl = videoUrl,
                    durationSeconds = 82 + (epNum * 2),
                    isFree = isFree,
                    coinPrice = if (isFree) 0 else 15,
                    likesCount = 4500 + (epNum * 350),
                    commentsCount = 110 + (epNum * 22),
                    teaserText = "[Dubbing Melayu 🇲🇾] Episod $epNum: $epTitle. Komedi korporat alih suara Melayu kelakar & puas hati!"
                )
            )
        }

        // 6. Drama 6: Rahsia Isteri Pengawal Peribadi (15 Episodes)
        val titles6 = listOf(
            "Pengawal Peribadi Paling Ditakuti", "Perkahwinan Rahsia Malam Hari", "Menyelamatkan Bos Dari Tembakan",
            "Identiti Sebenar Isteri Tersembunyi", "Gadis Lemah Lembut Berubah Singa", "Pukulan Maut Di Tempat Letak Kereta",
            "Suami Mula Merasa Sangsi", "Misi Rahsia Di Hotel Antarabangsa", "Pertembungan Dua Agen Terbaik",
            "Pengakuan Di Ruang Rawatan Cemas", "Cinta Sebenar Mengatasi Tugas", "Perangkap Musuh Antarabangsa",
            "Gandingan Suami Isteri Menumpaskan Dalang", "Kedamaian Selepas Badai Berlalu", "Janji Sehidup Semati (Kemuncak Akhir)"
        )
        titles6.forEachIndexed { idx, epTitle ->
            val epNum = idx + 1
            val isFree = epNum <= 3
            val videoUrl = if (epNum % 2 == 0) driveVideoUrl else youtubeVideoUrl
            all127Episodes.add(
                EpisodeEntity(
                    id = "ep_isteri_${String.format("%02d", epNum)}",
                    dramaId = "drama_isteri_pengawal",
                    episodeNumber = epNum,
                    title = epTitle,
                    videoUrl = videoUrl,
                    durationSeconds = 85 + (epNum * 2),
                    isFree = isFree,
                    coinPrice = if (isFree) 0 else 15,
                    likesCount = 3900 + (epNum * 280),
                    commentsCount = 85 + (epNum * 16),
                    teaserText = "[Dubbing Melayu 🇲🇾] Episod $epNum: $epTitle. Aksi romantik pengawal peribadi alih suara Melayu."
                )
            )
        }

        // 7. Drama 7: Ibu Mertua Angkuh Berlutut (12 Episodes)
        val titles7 = listOf(
            "Menantu Menyuap Lantai Rumah", "Ibu Mertua Menuntut Harta Pusaka", "Menghalau Menantu Keluar Rumah",
            "Peguam Negara Hadir Menyerahkan Dokumen", "Banglo Mewah Rupanya Milik Menantu!", "Syarikat Keluarga Disita Bank",
            "Menantu Duduk Di Kerusi Pengerusi", "Ibu Mertua Tergamam & Menangis", "Rayuan Kemaafan Di Hadapan Tetamu",
            "Mertua Berlutut Memohon Bantuan", "Hati Mulia Menantu Memaafkan", "Keharmonian Keluarga Terpelihara (Episod Akhir)"
        )
        titles7.forEachIndexed { idx, epTitle ->
            val epNum = idx + 1
            val isFree = epNum <= 3
            val videoUrl = if (epNum % 2 == 0) youtubeVideoUrl else driveVideoUrl
            all127Episodes.add(
                EpisodeEntity(
                    id = "ep_mertua_${String.format("%02d", epNum)}",
                    dramaId = "drama_ibu_mertua",
                    episodeNumber = epNum,
                    title = epTitle,
                    videoUrl = videoUrl,
                    durationSeconds = 80 + (epNum * 2),
                    isFree = isFree,
                    coinPrice = if (isFree) 0 else 15,
                    likesCount = 7200 + (epNum * 510),
                    commentsCount = 210 + (epNum * 40),
                    teaserText = "[Dubbing Melayu 🇲🇾] Episod $epNum: $epTitle. Drama keluarga paling viral alih suara Melayu!"
                )
            )
        }

        // Total episodes = 20 + 20 + 20 + 20 + 20 + 15 + 12 = 127
        episodeDao.insertEpisodes(all127Episodes)

        // Seed default reviews
        reviewDao.insertReview(
            ReviewEntity(
                id = "rev_ceo_1",
                dramaId = "drama_ceo_gadis",
                userId = "user_rev_01",
                username = "Farah Nabilah (Peminat Dubbing)",
                rating = 5,
                reviewText = "Alih suara Bahasa Melayu sangat berkualiti tinggi dan sepadan dengan pergerakan bibir! Suara Tengku Zaril sangat maskulin dan penuh karisma.",
                createdAt = System.currentTimeMillis() - 86400000
            )
        )
        reviewDao.insertReview(
            ReviewEntity(
                id = "rev_jutawan_1",
                dramaId = "drama_jutawan_rahsia",
                userId = "user_rev_02",
                username = "Hafiz Shah",
                rating = 5,
                reviewText = "Audio dubbing Melayu jelas dan dramatik. Rasa macam tonton drama televisyen Melayu sebenar!",
                createdAt = System.currentTimeMillis() - 43200000
            )
        )
        reviewDao.insertReview(
            ReviewEntity(
                id = "rev_mertua_1",
                dramaId = "drama_ibu_mertua",
                userId = "user_rev_03",
                username = "Siti Zulaikha",
                rating = 5,
                reviewText = "Puas hati sangat episod ibu mertua berlutut tu! Suara dubbing mak mertua memang menjadi sangat garang dia.",
                createdAt = System.currentTimeMillis() - 21600000
            )
        )

        // Starter comments
        commentDao.insertComment(CommentEntity("c_1", "ep_ceo_01", "Aina Syahirah", "", "Best gila ada dubbing Melayu macam ni! Senang faham tak payah baca subtitle! 🔥🇲🇾", System.currentTimeMillis() - 3600000, 48))
        commentDao.insertComment(CommentEntity("c_2", "ep_ceo_01", "Farhan KL", "", "Kualiti video HD, suara alih bahasa Melayu memang mantap.", System.currentTimeMillis() - 7200000, 32))
        commentDao.insertComment(CommentEntity("c_3", "ep_jutawan_01", "Kamal Affandi", "", "Hebat Dani Mikail! Jalan cerita laju dan tak bosan.", System.currentTimeMillis() - 1800000, 56))

        // Starter claimed voucher
        voucherDao.insertVoucher(
            ClaimedVoucherEntity(
                id = "v_starter_01",
                userId = "user_default_01",
                voucherTitle = "Baucar Diskaun RM10 Nasadef Shop",
                voucherCode = "NASADEF-RM10-DUBBING",
                discountDescription = "Potongan RM10 untuk semua barangan di Kedai Rasmi Nasadef.",
                pointsCost = 100,
                shopUrl = "https://nasadef-website.web.app/",
                claimedAt = System.currentTimeMillis() - 86400000,
                isUsed = false
            )
        )

        // Starter topup transaction
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

    suspend fun unlockEpisode(dramaId: String, episodeId: String, coinPrice: Int): Boolean =
        withContext(Dispatchers.IO) {
            val user = userDao.getUserSync("user_default_01") ?: return@withContext false
            if (user.coinBalance < coinPrice) {
                return@withContext false
            }

            userDao.deductCoins(user.id, coinPrice)
            userDao.addRewardPoints(user.id, 10)

            unlockedDao.insertUnlock(
                UnlockedEpisodeEntity(
                    id = UUID.randomUUID().toString(),
                    userId = user.id,
                    dramaId = dramaId,
                    episodeId = episodeId,
                    coinsSpent = coinPrice
                )
            )
            true
        }

    suspend fun submitDramaReview(dramaId: String, rating: Int, reviewText: String, username: String): Unit =
        withContext(Dispatchers.IO) {
            val review = ReviewEntity(
                id = UUID.randomUUID().toString(),
                dramaId = dramaId,
                userId = "user_default_01",
                username = username,
                rating = rating,
                reviewText = reviewText
            )
            reviewDao.insertReview(review)
            userDao.addRewardPoints("user_default_01", 15)

            // Recalculate average rating & reviews count
            val dramaReviews = reviewDao.getReviewsForDramaSync(dramaId)
            val avg = if (dramaReviews.isNotEmpty()) {
                dramaReviews.map { it.rating }.average()
            } else {
                5.0
            }
            dramaDao.updateRating(dramaId, (avg * 10).toInt() / 10.0, dramaReviews.size)
        }

    suspend fun claimShopVoucher(reward: ShopVoucherReward): Result<ClaimedVoucherEntity> =
        withContext(Dispatchers.IO) {
            val user = userDao.getUserSync("user_default_01")
                ?: return@withContext Result.failure(Exception("Pengguna tidak dijumpai."))

            if (user.rewardPoints < reward.pointsRequired) {
                return@withContext Result.failure(Exception("Mata ganjaran tidak mencukupi."))
            }

            userDao.deductRewardPoints(user.id, reward.pointsRequired)

            val randomSuffix = Random.nextInt(1000, 9999)
            val generatedCode = "${reward.codePrefix}-$randomSuffix"

            val voucher = ClaimedVoucherEntity(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                voucherTitle = reward.title,
                voucherCode = generatedCode,
                discountDescription = reward.discountDesc,
                pointsCost = reward.pointsRequired,
                shopUrl = "https://nasadef-website.web.app/"
            )
            voucherDao.insertVoucher(voucher)
            Result.success(voucher)
        }

    suspend fun watchRewardedAd(ad: SponsoredAd): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val user = userDao.getUserSync("user_default_01") ?: return@withContext Pair(0, 0)
        userDao.recordAdWatchedReward(user.id, ad.rewardCoins, ad.rewardPoints)
        Pair(ad.rewardCoins, ad.rewardPoints)
    }

    suspend fun submitTopupReceipt(
        coinPackage: CoinPackage,
        paymentMethod: String,
        accountNumber: String,
        referenceNumber: String,
        receiptNote: String
    ) = withContext(Dispatchers.IO) {
        val transaction = TopupTransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = "user_default_01",
            packageName = "${coinPackage.name} (${coinPackage.coins + coinPackage.bonusCoins} Syiling)",
            amountMyr = coinPackage.amountMyr,
            coinsReward = coinPackage.coins,
            bonusCoins = coinPackage.bonusCoins,
            paymentMethod = paymentMethod,
            accountNumber = accountNumber,
            referenceNumber = referenceNumber.trim(),
            receiptNote = receiptNote.trim(),
            status = "MENUNGGU_PENGESAHAN"
        )
        transactionDao.insertTransaction(transaction)
    }

    suspend fun approveTopupTransaction(transaction: TopupTransactionEntity) =
        withContext(Dispatchers.IO) {
            transactionDao.updateTransactionStatus(
                transactionId = transaction.id,
                status = "BERJAYA",
                reviewedAt = System.currentTimeMillis()
            )
            val totalCoinsToAdd = transaction.coinsReward + transaction.bonusCoins
            userDao.addCoins(transaction.userId, totalCoinsToAdd)
            userDao.addRewardPoints(transaction.userId, totalCoinsToAdd / 2)
        }

    suspend fun rejectTopupTransaction(transactionId: String) = withContext(Dispatchers.IO) {
        transactionDao.updateTransactionStatus(
            transactionId = transactionId,
            status = "DITOLAK",
            reviewedAt = System.currentTimeMillis()
        )
    }

    suspend fun toggleLike(episodeId: String, currentlyLiked: Boolean): Unit =
        withContext(Dispatchers.IO) {
            if (currentlyLiked) {
                likeDao.deleteLike("user_default_01", episodeId)
                episodeDao.updateLikeCount(episodeId, -1)
            } else {
                likeDao.insertLike(LikeEntity(UUID.randomUUID().toString(), "user_default_01", episodeId))
                episodeDao.updateLikeCount(episodeId, 1)
            }
        }

    suspend fun addComment(episodeId: String, username: String, content: String): Unit =
        withContext(Dispatchers.IO) {
            val comment = CommentEntity(
                id = UUID.randomUUID().toString(),
                episodeId = episodeId,
                username = username,
                content = content
            )
            commentDao.insertComment(comment)
        }

    suspend fun addNewDrama(
        title: String,
        genre: String,
        synopsis: String,
        totalEpisodes: Int,
        freeEpisodesCount: Int,
        defaultCoinPrice: Int
    ): Unit = withContext(Dispatchers.IO) {
        val dramaId = "drama_custom_${System.currentTimeMillis()}"
        val drama = DramaEntity(
            id = dramaId,
            title = title,
            genre = genre,
            synopsis = synopsis,
            posterResName = "poster_ceo_gadis_1787165470282",
            totalEpisodes = totalEpisodes,
            rating = 5.0,
            reviewsCount = 0,
            viewsCount = 100,
            isFeatured = false
        )
        dramaDao.insertDrama(drama)

        val newEpisodes = (1..totalEpisodes).map { epNum ->
            val isFree = epNum <= freeEpisodesCount
            EpisodeEntity(
                id = "ep_${dramaId}_$epNum",
                dramaId = dramaId,
                episodeNumber = epNum,
                title = "Episod $epNum: $title (Alih Suara Melayu)",
                videoUrl = if (epNum % 2 == 0) "https://youtu.be/EvwdsI9G6-o" else "https://drive.google.com/file/d/1KTWIYxRnCbmhFE_sQoGutMMVFWhrnM6s/view?usp=drive_link",
                durationSeconds = 90,
                isFree = isFree,
                coinPrice = if (isFree) 0 else defaultCoinPrice,
                likesCount = 0,
                commentsCount = 0,
                teaserText = "[Dubbing Melayu 🇲🇾] Episod $epNum drama $title."
            )
        }
        episodeDao.insertEpisodes(newEpisodes)
    }

    suspend fun deleteDrama(dramaId: String): Unit = withContext(Dispatchers.IO) {
        dramaDao.deleteDrama(dramaId)
    }
}
