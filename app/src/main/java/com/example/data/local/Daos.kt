package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClaimedVoucherEntity
import com.example.data.model.CommentEntity
import com.example.data.model.DramaEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.LikeEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.TopupTransactionEntity
import com.example.data.model.UnlockedEpisodeEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserSync(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET coinBalance = coinBalance + :coins WHERE id = :userId")
    suspend fun addCoins(userId: String, coins: Int)

    @Query("UPDATE users SET coinBalance = coinBalance - :coins WHERE id = :userId")
    suspend fun deductCoins(userId: String, coins: Int)

    @Query("UPDATE users SET rewardPoints = rewardPoints + :points WHERE id = :userId")
    suspend fun addRewardPoints(userId: String, points: Int)

    @Query("UPDATE users SET rewardPoints = rewardPoints - :points WHERE id = :userId")
    suspend fun deductRewardPoints(userId: String, points: Int)

    @Query("UPDATE users SET coinBalance = coinBalance + :coins, rewardPoints = rewardPoints + :points, totalAdsWatched = totalAdsWatched + 1 WHERE id = :userId")
    suspend fun recordAdWatchedReward(userId: String, coins: Int, points: Int)
}

@Dao
interface DramaDao {
    @Query("SELECT * FROM dramas ORDER BY isFeatured DESC, rating DESC")
    fun getAllDramas(): Flow<List<DramaEntity>>

    @Query("SELECT * FROM dramas WHERE id = :dramaId LIMIT 1")
    fun getDramaById(dramaId: String): Flow<DramaEntity?>

    @Query("SELECT * FROM dramas WHERE id = :dramaId LIMIT 1")
    suspend fun getDramaByIdSync(dramaId: String): DramaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrama(drama: DramaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDramas(dramas: List<DramaEntity>)

    @Query("DELETE FROM dramas WHERE id = :dramaId")
    suspend fun deleteDrama(dramaId: String)

    @Query("UPDATE dramas SET rating = :rating, reviewsCount = :reviewsCount WHERE id = :dramaId")
    suspend fun updateRating(dramaId: String, rating: Double, reviewsCount: Int)
}

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE dramaId = :dramaId ORDER BY episodeNumber ASC")
    fun getEpisodesForDrama(dramaId: String): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE dramaId = :dramaId ORDER BY episodeNumber ASC")
    suspend fun getEpisodesForDramaSync(dramaId: String): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE id = :episodeId LIMIT 1")
    suspend fun getEpisodeById(episodeId: String): EpisodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    @Query("DELETE FROM episodes WHERE id = :episodeId")
    suspend fun deleteEpisode(episodeId: String)

    @Query("UPDATE episodes SET likesCount = likesCount + :delta WHERE id = :episodeId")
    suspend fun updateLikeCount(episodeId: String, delta: Int)
}

@Dao
interface UnlockedEpisodeDao {
    @Query("SELECT * FROM unlocked_episodes WHERE userId = :userId")
    fun getUnlockedEpisodes(userId: String): Flow<List<UnlockedEpisodeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_episodes WHERE userId = :userId AND episodeId = :episodeId)")
    fun isEpisodeUnlocked(userId: String, episodeId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_episodes WHERE userId = :userId AND episodeId = :episodeId)")
    suspend fun isEpisodeUnlockedSync(userId: String, episodeId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnlock(unlockedEpisode: UnlockedEpisodeEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM topup_transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<TopupTransactionEntity>>

    @Query("SELECT * FROM topup_transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserTransactions(userId: String): Flow<List<TopupTransactionEntity>>

    @Query("SELECT * FROM topup_transactions WHERE status = 'MENUNGGU_PENGESAHAN' ORDER BY createdAt DESC")
    fun getPendingTransactions(): Flow<List<TopupTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TopupTransactionEntity)

    @Query("UPDATE topup_transactions SET status = :status, reviewedAt = :reviewedAt WHERE id = :transactionId")
    suspend fun updateTransactionStatus(transactionId: String, status: String, reviewedAt: Long)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE episodeId = :episodeId ORDER BY createdAt DESC")
    fun getCommentsForEpisode(episodeId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
}

@Dao
interface LikeDao {
    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND episodeId = :episodeId)")
    fun isLiked(userId: String, episodeId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: LikeEntity)

    @Query("DELETE FROM likes WHERE userId = :userId AND episodeId = :episodeId")
    suspend fun deleteLike(userId: String, episodeId: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE dramaId = :dramaId ORDER BY createdAt DESC")
    fun getReviewsForDrama(dramaId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE dramaId = :dramaId ORDER BY createdAt DESC")
    suspend fun getReviewsForDramaSync(dramaId: String): List<ReviewEntity>

    @Query("SELECT * FROM reviews WHERE dramaId = :dramaId AND userId = :userId LIMIT 1")
    fun getUserReviewForDrama(dramaId: String, userId: String): Flow<ReviewEntity?>

    @Query("SELECT AVG(rating) FROM reviews WHERE dramaId = :dramaId")
    suspend fun getAverageRatingForDrama(dramaId: String): Double?

    @Query("SELECT COUNT(*) FROM reviews WHERE dramaId = :dramaId")
    suspend fun getReviewCountForDrama(dramaId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
}

@Dao
interface ClaimedVoucherDao {
    @Query("SELECT * FROM claimed_vouchers WHERE userId = :userId ORDER BY claimedAt DESC")
    fun getUserVouchers(userId: String): Flow<List<ClaimedVoucherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: ClaimedVoucherEntity)

    @Query("UPDATE claimed_vouchers SET isUsed = 1 WHERE id = :voucherId")
    suspend fun markVoucherAsUsed(voucherId: String)
}
