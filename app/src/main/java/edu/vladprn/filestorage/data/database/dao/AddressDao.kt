package edu.vladprn.filestorage.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.vladprn.filestorage.data.database.entity.AddressEntity
import edu.vladprn.filestorage.data.database.entity.FileEntity

@Dao
interface AddressDao {

    @Query("SELECT * FROM addresses")
    suspend fun getAllAddresses(): List<AddressEntity>

    @Query("SELECT * FROM addresses WHERE fileId = :fileId")
    suspend fun getAddressesForFile(fileId: Long): List<AddressEntity>

    @Insert
    suspend fun insertAddresses(addresses: List<AddressEntity>)
}