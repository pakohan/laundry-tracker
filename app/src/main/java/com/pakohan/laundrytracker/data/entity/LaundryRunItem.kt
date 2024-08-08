package com.pakohan.laundrytracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "laundry_run_item",
    primaryKeys = ["laundry_run_id", "laundry_item_id"],
    indices = [Index(value = ["laundry_item_id"])],
    foreignKeys = [
        ForeignKey(
            entity = LaundryRun::class,
            childColumns = ["laundry_run_id"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = LaundryItem::class,
            childColumns = ["laundry_item_id"],
            parentColumns = ["id"],
        ),
    ],
)
data class LaundryRunItem(
    @ColumnInfo(name = "laundry_run_id") val laundryRunId: Int = 0,
    @ColumnInfo(name = "laundry_item_id") val laundryItemId: Int = 0,
    val quantity: Int = 0,
    @ColumnInfo(name = "retrieved_quantity") val retrievedQuantity: Int = 0,
)

data class EnrichedLaundryRunItem(
    @ColumnInfo(name = "laundry_run_id") val laundryRunId: Int,
    @ColumnInfo(name = "laundry_item_id") val laundryItemId: Int,
    @ColumnInfo(name = "laundry_item_name") val laundryItemName: String,
    val quantity: Int,
    @ColumnInfo(name = "retrieved_quantity") val retrievedQuantity: Int,
)

@Dao
interface LaundryRunItemDao {
    @Query(
        """SELECT laundry_run.id as laundry_run_id
                , laundry_item.id as laundry_item_id
                , laundry_item.name as laundry_item_name
                , COALESCE(laundry_run_item.quantity, 0 ) as quantity
                , COALESCE(laundry_run_item.retrieved_quantity, 0 ) as retrieved_quantity
           FROM laundry_run, laundry_item
           LEFT JOIN laundry_run_item
            ON (laundry_run.id = laundry_run_item.laundry_run_id AND laundry_item.id = laundry_run_item.laundry_item_id)
            WHERE laundry_run.id = :laundryRunId
             AND (laundry_run.started_at IS NULL OR laundry_run_item.quantity > 0)
           ORDER BY lower(laundry_item.name) ASC""",
    )
    fun getAllOfRunFlow(laundryRunId: Int): Flow<List<EnrichedLaundryRunItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(laundryItem: LaundryRunItem)

    @Delete
    suspend fun delete(laundryItem: LaundryRunItem)
}

interface LaundryRunItemRepository {
    fun getAllOfRunFlow(laundryRunId: Int): Flow<List<EnrichedLaundryRunItem>>
    suspend fun create(laundryRunItem: LaundryRunItem)
    suspend fun delete(laundryRunItem: LaundryRunItem)
}

class DatabaseLaundryRunItemRepository(private val laundryRunItemDao: LaundryRunItemDao) : LaundryRunItemRepository {
    override fun getAllOfRunFlow(laundryRunId: Int): Flow<List<EnrichedLaundryRunItem>> =
        laundryRunItemDao.getAllOfRunFlow(laundryRunId)

    override suspend fun create(laundryRunItem: LaundryRunItem) = laundryRunItemDao.insert(laundryRunItem)
    override suspend fun delete(laundryRunItem: LaundryRunItem) = laundryRunItemDao.delete(laundryRunItem)
}
