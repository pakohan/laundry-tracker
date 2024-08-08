package com.pakohan.laundrytracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pakohan.laundrytracker.data.entity.LaundryItem
import com.pakohan.laundrytracker.data.entity.LaundryItemDao
import com.pakohan.laundrytracker.data.entity.LaundryRun
import com.pakohan.laundrytracker.data.entity.LaundryRunDao
import com.pakohan.laundrytracker.data.entity.LaundryRunItem
import com.pakohan.laundrytracker.data.entity.LaundryRunItemDao

@Database(
    entities = [LaundryItem::class, LaundryRun::class, LaundryRunItem::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun laundryItemDao(): LaundryItemDao
    abstract fun laundryRunDao(): LaundryRunDao
    abstract fun laundryRunItemDao(): LaundryRunItemDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "laundry_database",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
