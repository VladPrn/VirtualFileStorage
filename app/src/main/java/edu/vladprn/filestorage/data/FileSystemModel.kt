package edu.vladprn.filestorage.data

import android.content.Context
import edu.vladprn.filestorage.domain.Constants
import edu.vladprn.filestorage.domain.model.FileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.RandomAccessFile
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.min

class FileSystemModel(
    private val context: Context
) {

    suspend fun writeFile(
        inputStream: InputStream,
        fileModel: FileModel,
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            inputStream.use { writeFileInternal(inputStream, fileModel) }
            true
        } catch (_: Throwable) {
            false
        }
    }

    suspend fun extractFile(fileModel: FileModel): File? = withContext(Dispatchers.IO) {
        try {
            val folder = File(context.filesDir, SHARE)
            val file = File(folder, TMP_FILE)
            folder.mkdir()
            file.outputStream().use { outputStream ->
                extractFileInternal(outputStream, fileModel)
            }

            file
        } catch (_: Throwable) {
            null
        }
    }

    private fun writeFileInternal(
        inputStream: InputStream,
        fileModel: FileModel,
    ) {
        val byteArray = ByteArray(Constants.SEGMENT_SIZE)

        var raf: RandomAccessFile? = null
        var currentStorageIndex: Int = -1
        var byteIndex = 0L

        try {
            fileModel.addresses?.forEach { address ->
                val bytePos = address * Constants.SEGMENT_SIZE
                val storageIndex = bytePos / STORAGE_SIZE
                val addressInStorage = bytePos % STORAGE_SIZE
                if (storageIndex != currentStorageIndex) {
                    raf?.close()
                    raf = getStorageFile(storageIndex, isWriteMode = true)
                    currentStorageIndex = storageIndex
                }

                raf?.seek(addressInStorage.toLong())
                inputStream.read(byteArray)

                val len = min(fileModel.size - byteIndex, Constants.SEGMENT_SIZE.toLong()).toInt()
                raf?.write(byteArray, 0, len)
                byteIndex += len
            }
        } finally {
            try {
                raf?.close()
            } catch (_: IOException) {
                // Nothing
            }
        }
    }

    private fun extractFileInternal(
        outputStream: OutputStream,
        fileModel: FileModel,
    ) {
        val byteArray = ByteArray(Constants.SEGMENT_SIZE)

        var raf: RandomAccessFile? = null
        var currentStorageIndex: Int = -1
        var byteIndex = 0L

        try {
            fileModel.addresses?.forEach { address ->
                val bytePos = address * Constants.SEGMENT_SIZE
                val storageIndex = bytePos / STORAGE_SIZE
                val addressInStorage = bytePos % STORAGE_SIZE
                if (storageIndex != currentStorageIndex) {
                    raf?.close()
                    raf = getStorageFile(storageIndex, isWriteMode = false)
                    currentStorageIndex = storageIndex
                }

                raf?.seek(addressInStorage.toLong())
                raf?.read(byteArray)

                val len = min(fileModel.size - byteIndex, Constants.SEGMENT_SIZE.toLong()).toInt()
                outputStream.write(byteArray, 0, len)
                byteIndex += len
            }
        } finally {
            try {
                raf?.close()
            } catch (_: IOException) {
                // Nothing
            }
        }
    }

    private fun getStorageFile(
        storageIndex: Int,
        isWriteMode: Boolean,
    ): RandomAccessFile {
        val folder = context.getDir(FOLDER, Context.MODE_PRIVATE)
        val file = File(folder, "$storageIndex.bin")

        val mode = if (isWriteMode) {
            "rw"
        } else {
            "r"
        }
        return RandomAccessFile(file, mode).apply {
            if (isWriteMode) {
                setLength(STORAGE_SIZE.toLong())
            }
        }
    }

    suspend fun extractToZip(
        fileModels: List<FileModel>,
        outputStream: OutputStream,
        onProgress: ((processedFiles: Int, totalFiles: Int) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val zipOutputStream = ZipOutputStream(outputStream)
            val totalFiles = fileModels.size

            fileModels.forEachIndexed { index, fileModel ->
                val entryName = fileModel.name
                zipOutputStream.putNextEntry(ZipEntry(entryName))

                extractFileInternal(zipOutputStream, fileModel)

                zipOutputStream.closeEntry()

                onProgress?.invoke(index + 1, totalFiles)
            }

            zipOutputStream.finish()
            zipOutputStream.close()
            true
        } catch (_: Throwable) {
            false
        }
    }

    companion object {
        private const val FOLDER = "storage"
        private const val SHARE = "share"
        private const val TMP_FILE = "tmp"
        private const val STORAGE_SIZE = 1024 * 1024 * 8
    }
}