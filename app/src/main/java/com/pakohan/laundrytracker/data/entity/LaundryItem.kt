package com.pakohan.laundrytracker.data.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Entity(tableName = "laundry_item")
data class LaundryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
)

@Dao
interface LaundryItemDao {
    @Query("SELECT * from laundry_item ORDER BY lower(name) ASC")
    fun getAllFlow(): Flow<List<LaundryItem>>

    @Query("SELECT * from laundry_item WHERE id = :id")
    suspend fun get(id: Int): LaundryItem?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(laundryItem: LaundryItem)

    @Update
    suspend fun update(laundryItem: LaundryItem)

    @Delete
    suspend fun delete(laundryItem: LaundryItem)
}

interface LaundryItemRepository {
    fun getAllFlow(): Flow<List<LaundryItem>>

    suspend fun get(id: Int): LaundryItem?

    suspend fun insert(laundryItem: LaundryItem)

    suspend fun update(laundryItem: LaundryItem)

    suspend fun delete(laundryItem: LaundryItem)
}

class DatabaseLaundryItemRepository(private val laundryItemDao: LaundryItemDao) : LaundryItemRepository {
    override fun getAllFlow(): Flow<List<LaundryItem>> = laundryItemDao.getAllFlow()

    override suspend fun get(id: Int): LaundryItem? = laundryItemDao.get(id)

    override suspend fun insert(laundryItem: LaundryItem) = laundryItemDao.insert(laundryItem)

    override suspend fun update(laundryItem: LaundryItem) = laundryItemDao.update(laundryItem)

    override suspend fun delete(laundryItem: LaundryItem) = laundryItemDao.delete(laundryItem)
}

class StaticLaundryItemRepository : LaundryItemRepository {
    private val table = mutableListOf(
        LaundryItem(
            0,
            "T-Shirt",
        ),
        LaundryItem(
            1,
            "Pants",
        ),
        LaundryItem(
            2,
            "Sweater",
        ),
        LaundryItem(
            3,
            "Towel",
        ),
    )

    override fun getAllFlow(): Flow<List<LaundryItem>> {
        return flowOf(table)
    }

    override suspend fun get(id: Int): LaundryItem {
        return table.find { it.id == id } ?: LaundryItem()
    }

    override suspend fun insert(laundryItem: LaundryItem) {
        var maxId = 0
        table.forEach {
            if (it.id > maxId) maxId = it.id
        }
        table + LaundryItem(
            maxId,
            laundryItem.name,
        )
    }

    override suspend fun update(laundryItem: LaundryItem) {
        val index = table.indexOfFirst { it.id == laundryItem.id }
        if (index == -1) return

        table[index] = laundryItem
    }

    override suspend fun delete(laundryItem: LaundryItem) {
        table.removeIf { it.id == laundryItem.id }
    }
}
