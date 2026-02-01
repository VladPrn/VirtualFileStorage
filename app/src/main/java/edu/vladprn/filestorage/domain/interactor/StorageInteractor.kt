package edu.vladprn.filestorage.domain.interactor

import android.net.Uri
import edu.vladprn.filestorage.data.FileSystemModel
import edu.vladprn.filestorage.data.repository.FileRepository
import edu.vladprn.filestorage.domain.model.FileModel
import edu.vladprn.filestorage.utils.FileUtils
import java.io.File

class StorageInteractor(
    private val fileRepository: FileRepository,
    private val storageAllocator: StorageAllocator,
    private val fileUtils: FileUtils,
    private val fileSystemModel: FileSystemModel,
) {

    private var isFirstSaving = true

    suspend fun saveFile(uri: Uri): Boolean {
        val fileInfo = fileUtils.getFileInfo(uri)
        if (fileInfo.size == 0L) return false

        var fileModel = FileModel(
            name = fileInfo.name.orEmpty(),
            mimeType = fileInfo.mimeType.orEmpty(),
            size = fileInfo.size
        )

        if (isFirstSaving) {
            val addresses = fileRepository.getAllAddresses()
            storageAllocator.fillAllocatedAddresses(addresses)
            isFirstSaving = false
        }

        val addresses = storageAllocator.allocateAddresses(fileModel)
        fileModel = fileModel.copy(addresses = addresses)

        if (!fileSystemModel.writeFile(uri, fileModel)) return false

        storageAllocator.fillAllocatedAddresses(addresses)
        fileRepository.insertFile(fileModel)
        return true
    }

    suspend fun loadFile(fileModel: FileModel): File? {
        val fileModelWithAddresses = fileRepository.getFileAddresses(fileModel)
        return fileSystemModel.extractFile(fileModelWithAddresses)
    }

    suspend fun deleteFile(fileModel: FileModel) = fileRepository.deleteFile(fileModel)

    suspend fun getAllFiles() = fileRepository.getAllFiles()
}