package edu.vladprn.filestorage.domain.interactor

import android.content.Context
import android.net.Uri
import edu.vladprn.filestorage.data.FileSystemModel
import edu.vladprn.filestorage.data.mapper.ImageCompressor
import edu.vladprn.filestorage.data.repository.FileRepository
import edu.vladprn.filestorage.domain.model.FileModel
import edu.vladprn.filestorage.utils.FileUtils
import java.io.File
import java.io.InputStream

class StorageInteractor(
    private val context: Context,
    private val fileRepository: FileRepository,
    private val storageAllocator: StorageAllocator,
    private val fileUtils: FileUtils,
    private val fileSystemModel: FileSystemModel,
    private val imageCompressor: ImageCompressor,
) {

    private var isFirstSaving = true

    suspend fun saveFile(
        uri: Uri,
        fileName: String? = null
    ): Boolean {
        val fileInfo = fileUtils.getFileInfo(uri)
        if (fileInfo.size == 0L) return false

        var fileModel = FileModel(
            name = fileName ?: fileInfo.name.orEmpty(),
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

        val inputStream = getInputStream(uri) ?: return false
        if (!fileSystemModel.writeFile(inputStream, fileModel)) return false

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

    suspend fun beforeSaveFile(uri: Uri) = imageCompressor.processImage(uri)

    private fun getInputStream(uri: Uri): InputStream? = try {
        context.contentResolver.openInputStream(uri)
    } catch (_: Throwable) {
        null
    }
}