package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ClaimedVoucherEntity
import com.example.data.model.CommentEntity
import com.example.data.model.DramaEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.LikeEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.TopupTransactionEntity
import com.example.data.model.UnlockedEpisodeEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        DramaEntity::class,
        EpisodeEntity::class,
        UnlockedEpisodeEntity::class,
        TopupTransactionEntity::class,
        CommentEntity::class,
        LikeEntity::class,
        ReviewEntity::class,
        ClaimedVoucherEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun dramaDao(): DramaDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun unlockedEpisodeDao(): UnlockedEpisodeDao
    abstract fun transactionDao(): TransactionDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun reviewDao(): ReviewDao
    abstract fun claimedVoucherDao(): ClaimedVoucherDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dramashort_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
