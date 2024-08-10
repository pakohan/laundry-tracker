package com.pakohan.laundrytracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "laundry_item")
data class LaundryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
)

data class EnrichedLaundryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    @ColumnInfo(name = "can_be_deleted") val canBeDeleted: Boolean,
)

@Dao
interface LaundryItemDao {
    @Query(
        """SELECT laundry_item.id
                , laundry_item.name
                , COALESCE(quantity, 0) = 0 as can_be_deleted
        FROM laundry_item
        LEFT JOIN (
            SELECT laundry_item_id
                , SUM(quantity) as quantity
            FROM laundry_run_item
            GROUP BY laundry_item_id
        ) laundry_run_item 
        ON (laundry_item.id = laundry_item_id)
        ORDER BY lower(name) ASC""",
    )
    fun getAllFlow(): Flow<List<EnrichedLaundryItem>>

    @Query("SELECT * FROM laundry_item WHERE id = :id")
    suspend fun get(id: Int): LaundryItem?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(laundryItem: LaundryItem)

    @Update
    suspend fun update(laundryItem: LaundryItem)

    @Delete
    suspend fun delete(laundryItem: LaundryItem)
}

interface LaundryItemRepository {
    fun getAllFlow(): Flow<List<EnrichedLaundryItem>>

    suspend fun get(id: Int): LaundryItem?

    suspend fun insert(laundryItem: LaundryItem)

    suspend fun update(laundryItem: LaundryItem)

    suspend fun delete(laundryItem: LaundryItem)
}

class DatabaseLaundryItemRepository(private val laundryItemDao: LaundryItemDao) : LaundryItemRepository {
    override fun getAllFlow(): Flow<List<EnrichedLaundryItem>> = laundryItemDao.getAllFlow()

    override suspend fun get(id: Int): LaundryItem? = laundryItemDao.get(id)

    override suspend fun insert(laundryItem: LaundryItem) = laundryItemDao.insert(laundryItem)

    override suspend fun update(laundryItem: LaundryItem) = laundryItemDao.update(laundryItem)

    override suspend fun delete(laundryItem: LaundryItem) = laundryItemDao.delete(laundryItem)
}
