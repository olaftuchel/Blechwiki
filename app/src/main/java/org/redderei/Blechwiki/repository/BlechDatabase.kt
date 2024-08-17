package org.redderei.Blechwiki.repository

import android.content.*
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.redderei.Blechwiki.gettersetter.*
import java.util.concurrent.Executors

@Database(entities = [LiedClass::class, BuchClass::class, KomponistClass::class, TitelClass::class], version = Constant.DB_VERSION, exportSchema = false)
abstract class BlechDatabase : RoomDatabase() {
    abstract fun BlechDao(): BlechDao?

    companion object {
        // marking the instance as volatile to ensure atomic access to the variable
        private var INSTANCE: BlechDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(BlechDatabase.Companion.NUMBER_OF_THREADS)

        fun getDatabase(context: Context): BlechDatabase? {
            if (BlechDatabase.INSTANCE == null) {
                synchronized(BlechDatabase::class.java) {
                    if (BlechDatabase.INSTANCE == null) {
                        BlechDatabase.INSTANCE = Room.databaseBuilder(context.applicationContext,
                            BlechDatabase::class.java, Constant.DB_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(BlechDatabase.Companion.sRoomDatabaseCallback)
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .build()
                    }
                }
            }
            return BlechDatabase.INSTANCE
        }

        /**
         * Override the onOpen method to populate the database.
         * For this sample, we clear the database every time it is created or opened.
         *
         *
         * If you want to populate the database only when the database is created for the 1st time,
         * override RoomDatabase.Callback()#onCreate
         */
        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                // If you want to keep data through app restarts,
                // comment out the following block
                // new PopulateDbAsync(INSTANCE).execute();
            }
        }
    }
}