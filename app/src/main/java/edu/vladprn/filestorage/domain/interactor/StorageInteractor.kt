package edu.vladprn.filestorage.domain.interactor

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import edu.vladprn.filestorage.data.FileSystemModel
import edu.vladprn.filestorage.data.mapper.ImageCompressor
import edu.vladprn.filestorage.data.repository.FileRepository
import edu.vladprn.filestorage.domain.model.FileModel
import edu.vladprn.filestorage.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

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

    suspend fun extractToZip(
        uri: Uri,
        onProgress: ((processedFiles: Int, totalFiles: Int) -> Unit)? = null,
    ): Boolean = withContext(Dispatchers.IO) {
        val fileModels = fileRepository.getAllFilesWithAddresses()
        val outputStream = getOutputStream(uri) ?: return@withContext false

        try {
            val zipOutputStream = ZipOutputStream(outputStream)
            val totalFiles = fileModels.size

            fileModels.forEachIndexed { index, fileModel ->
                val entryName = fileModel.name
                zipOutputStream.putNextEntry(ZipEntry(entryName))

                fileSystemModel.extractFileInternal(zipOutputStream, fileModel)

                zipOutputStream.closeEntry()

                onProgress?.invoke(index + 1, totalFiles)
            }

            zipOutputStream.flush()
            zipOutputStream.close()
            true
        } catch (_: Throwable) {
            false
        }
    }

    suspend fun importFromZip(
        uri: Uri,
        onProgress: ((processedFiles: Int, totalFiles: Int) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val inputStream = getInputStream(uri) ?: return@withContext false
        val file = fileSystemModel.getTempFile()
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        val zipFile = ZipFile(file)

        var processedFiles = 0
        val totalFiles = zipFile.size()
        val entries = zipFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()

            val ext = entry.name.substringAfterLast(
                delimiter = '.',
                missingDelimiterValue = ""
            )
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)

            var fileModel = FileModel(
                name = entry.name,
                mimeType = mimeType.orEmpty(),
                size = entry.size
            )

            if (isFirstSaving) {
                val addresses = fileRepository.getAllAddresses()
                storageAllocator.fillAllocatedAddresses(addresses)
                isFirstSaving = false
            }

            val addresses = storageAllocator.allocateAddresses(fileModel)
            fileModel = fileModel.copy(addresses = addresses)

            val inputStream = zipFile.getInputStream(entry)
            try {
                inputStream.use {
                    fileSystemModel.writeFileInternal(inputStream, fileModel)
                }
                storageAllocator.fillAllocatedAddresses(addresses)
                fileRepository.insertFile(fileModel)
            } catch (_: Throwable) {
                // Nothing
            }

            onProgress?.invoke(++processedFiles, totalFiles)
        }

        try {
            zipFile.close()
            true
        } catch (_: IOException) {
            return@withContext false
        }
    }

    private fun getInputStream(uri: Uri): InputStream? = try {
        context.contentResolver.openInputStream(uri)
    } catch (_: Throwable) {
        null
    }

    private fun getOutputStream(uri: Uri): OutputStream? = try {
        context.contentResolver.openOutputStream(uri)
    } catch (_: Throwable) {
        null
    }
}