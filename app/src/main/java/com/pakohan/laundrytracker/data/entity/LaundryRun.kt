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
import java.util.Date

@Entity(tableName = "laundry_run")
data class LaundryRun(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "started_at") val startedAt: Date? = null,
)

data class EnrichedLaundryRun(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "started_at") val startedAt: Date? = null,
    val quantity: Int,
    @ColumnInfo(name = "retrieved_quantity") val retrievedQuantity: Int,
)

@Dao
interface LaundryRunDao {
    @Query(
        """SELECT laundry_run.id
    , laundry_run.started_at
    , quantity
    , retrieved_quantity
FROM laundry_run
LEFT JOIN (
    SELECT laundry_run_item.laundry_run_id
        , SUM(laundry_run_item.quantity) as quantity
        , SUM(laundry_run_item.retrieved_quantity) as retrieved_quantity
    FROM laundry_run_item
    GROUP BY laundry_run_id
) laundry_run_item
ON (laundry_run.id = laundry_run_item.laundry_run_id)
ORDER BY laundry_run.id ASC""",
    )
    fun getAllFlow(): Flow<List<EnrichedLaundryRun>>

    @Query("SELECT * FROM laundry_run WHERE id = :id")
    suspend fun get(id: Int): LaundryRun?

    @Query(
        """SELECT laundry_run.id
    , laundry_run.started_at
    , quantity
    , retrieved_quantity
FROM laundry_run
LEFT JOIN (
    SELECT laundry_run_item.laundry_run_id
        , SUM(laundry_run_item.quantity) as quantity
        , SUM(laundry_run_item.retrieved_quantity) as retrieved_quantity
    FROM laundry_run_item
    WHERE laundry_run_item.laundry_run_id = :id
) laundry_run_item
ON (laundry_run.id = laundry_run_item.laundry_run_id)
WHERE laundry_run.id = :id""",
    )
    suspend fun getEnriched(id: Int): EnrichedLaundryRun?

    @Query("SELECT * FROM laundry_run WHERE id = :id")
    fun getFlow(id: Int): Flow<LaundryRun?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(laundryItem: LaundryRun): Long

    @Update
    suspend fun update(laundryItem: LaundryRun)

    @Delete
    suspend fun delete(laundryItem: LaundryRun)

}

interface LaundryRunRepository {
    fun getAllFlow(): Flow<List<EnrichedLaundryRun>>
    fun getFlow(laundryRunId: Int): Flow<LaundryRun?>
    suspend fun get(laundryRunId: Int): LaundryRun?
    suspend fun getEnriched(laundryRunId: Int): EnrichedLaundryRun?
    suspend fun create(laundryRun: LaundryRun): Long
    suspend fun delete(laundryRun: LaundryRun)
    suspend fun update(laundryRun: LaundryRun)
}

class DatabaseLaundryRunRepository(private val laundryRunDao: LaundryRunDao) : LaundryRunRepository {
    override fun getAllFlow(): Flow<List<EnrichedLaundryRun>> = laundryRunDao.getAllFlow()
    override suspend fun create(laundryRun: LaundryRun): Long = laundryRunDao.insert(laundryRun)
    override suspend fun delete(laundryRun: LaundryRun) = laundryRunDao.delete(laundryRun)
    override suspend fun get(laundryRunId: Int): LaundryRun? = laundryRunDao.get(laundryRunId)
    override suspend fun getEnriched(laundryRunId: Int): EnrichedLaundryRun? = laundryRunDao.getEnriched(laundryRunId)
    override fun getFlow(laundryRunId: Int): Flow<LaundryRun?> = laundryRunDao.getFlow(laundryRunId)
    override suspend fun update(laundryRun: LaundryRun) = laundryRunDao.update(laundryRun)
}
