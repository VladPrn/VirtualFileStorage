package edu.vladprn.filestorage.data.repository

import androidx.room.withTransaction
import edu.vladprn.filestorage.data.database.AppDatabase
import edu.vladprn.filestorage.data.mapper.AddressEntityMapper
import edu.vladprn.filestorage.data.mapper.FileEntityMapper
import edu.vladprn.filestorage.domain.model.FileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileRepository(
    private val appDatabase: AppDatabase,
    private val fileEntityMapper: FileEntityMapper,
    private val addressEntityMapper: AddressEntityMapper
) {

    private val fileDao = appDatabase.fileDao()
    private val addressDao = appDatabase.addressDao()

    suspend fun getAllFiles(): List<FileModel> =
        withContext(Dispatchers.IO) {
            val list = fileDao.getAllFiles()
            fileEntityMapper.mapFromEntities(list)
        }

    suspend fun getAllFilesWithAddresses(): List<FileModel> =
        withContext(Dispatchers.IO) {
            val list = fileDao.getAllFilesWithAddresses()
            fileEntityMapper.mapFromAddressesEntities(list)
        }

    suspend fun insertFile(fileModel: FileModel): Long =
        withContext(Dispatchers.IO) {
            appDatabase.withTransaction {
                val fileEntity = fileEntityMapper.mapToEntity(fileModel)
                val id = fileDao.insertFile(fileEntity)
                val fileModelWithId = fileModel.copy(id = id)
                val addresses = addressEntityMapper.mapToEntity(fileModelWithId)
                if (addresses != null) {
                    addressDao.insertAddresses(addresses)
                }
                return@withTransaction id
            }
        }

    suspend fun getAllAddresses(): List<Int> =
        withContext(Dispatchers.IO) {
            val addressesEntities = addressDao.getAllAddresses()
            return@withContext addressEntityMapper.mapFromEntity(addressesEntities)
        }

    suspend fun getFileAddresses(fileModel: FileModel) =
        withContext(Dispatchers.IO) {
            val addressesEntities = addressDao.getAddressesForFile(fileId = fileModel.id)
            val addresses = addressEntityMapper.mapFromEntity(addressesEntities)
            return@withContext fileModel.copy(addresses = addresses)
        }

    suspend fun deleteFile(fileModel: FileModel) =
        withContext(Dispatchers.IO) {
            val fileEntity = fileEntityMapper.mapToEntity(fileModel)
            fileDao.deleteFile(fileEntity)
        }
}