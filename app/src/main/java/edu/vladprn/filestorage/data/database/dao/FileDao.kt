package edu.vladprn.filestorage.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import edu.vladprn.filestorage.data.database.entity.FileEntity
import edu.vladprn.filestorage.data.database.entity.FileWithAddressesEntity

@Dao
interface FileDao {

    @Query("SELECT * FROM files")
    suspend fun getAllFiles(): List<FileEntity>

    @Query("SELECT * FROM files")
    suspend fun getAllFilesWithAddresses(): List<FileWithAddressesEntity>

    @Insert
    suspend fun insertFile(file: FileEntity): Long

    @Delete
    suspend fun deleteFile(file: FileEntity)
}