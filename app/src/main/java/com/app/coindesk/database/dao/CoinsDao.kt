package com.app.coindesk.database.dao

import com.app.coindesk.entity.Coins
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoinsDao {
    @Query("SELECT * FROM coins")
    fun getCoins(): Array<Coins>

    @Insert
    fun insertCoins(launchListList: Coins): Long

    @Query("DELETE FROM coins")
    fun truncateCoins()
}
