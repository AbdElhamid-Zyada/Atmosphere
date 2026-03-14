package com.example.atmoshpere.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM favorite_locations")
    fun getAllLocations(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: FavoriteLocation)

    @Delete
    suspend fun deleteLocation(location: FavoriteLocation)
}
