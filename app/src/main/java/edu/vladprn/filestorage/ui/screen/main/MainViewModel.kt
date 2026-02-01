package edu.vladprn.filestorage.ui.screen.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.vladprn.filestorage.R
import edu.vladprn.filestorage.data.ResourceManager
import edu.vladprn.filestorage.domain.interactor.StorageInteractor
import edu.vladprn.filestorage.domain.model.FileModel
import edu.vladprn.filestorage.ui.screen.main.compose.FileItemAction
import edu.vladprn.filestorage.utils.FileUtils
import edu.vladprn.filestorage.utils.IntentUtils
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class MainViewModel(
    private val storageInteractor: StorageInteractor,
    private val resourceManager: ResourceManager,
    private val fileUtils: FileUtils,
    private val intentUtils: IntentUtils,
) : ViewModel(), MainListener {

    var state by mutableStateOf(MainViewState())
    var snackbarHostState = SnackbarHostState()

    private var files: List<FileModel>? = null
    private var galleryFileModel: FileModel? = null
    private var unloadFileModel: FileModel? = null

    private var onFilePick: (() -> Unit) = {}
    private var onFileUnload: ((String) -> Unit) = {}

    init {
        viewModelScope.launch {
            loadFiles()
        }
    }

    fun setOnFilePicker(onFilePick: () -> Unit) {
        this.onFilePick = onFilePick
    }

    fun setOnFileUnload(onFileUnload: (String) -> Unit) {
        this.onFileUnload = onFileUnload
    }

    fun openFileUri(uri: Uri?) = viewModelScope.launch {
        uri ?: return@launch

        state = state.copy(inProgress = true)
        val saveFileResult = storageInteractor.saveFile(uri)
        state = state.copy(inProgress = false)

        if (saveFileResult) {
            loadFiles()
        } else {
            showError()
        }
    }

    fun saveFileUri(uri: Uri?) = viewModelScope.launch {
        uri ?: return@launch
        val fileModel = unloadFileModel ?: return@launch

        state = state.copy(inProgress = true)
        val fileIO = storageInteractor.loadFile(fileModel)
        state = state.copy(inProgress = false)

        if (fileIO != null) {
            fileUtils.saveFile(fileIO, uri)
        } else {
            showError()
        }
    }

    override fun onSearchTextChanged(query: String) {
        state = state.copy(
            searchQuery = query,
            items = buildItems(
                files = files,
                query = query
            )
        )
    }

    override fun onAddFileClick() {
        onFilePick()
    }

    override fun onGalleryLeftSwipe() {
        val fileModel = findNextFile(
            files = files ?: return,
            currentFile = galleryFileModel ?: return,
            iterator = { it + 1 }
        ) ?: return

        openFileInGallery(fileModel)
    }

    override fun onGalleryRightSwipe() {
        val fileModel = findNextFile(
            files = files ?: return,
            currentFile = galleryFileModel ?: return,
            iterator = { it - 1 }
        ) ?: return

        openFileInGallery(fileModel)
    }

    override fun onItemAction(
        file: FileUIModel,
        fileItemAction: FileItemAction
    ) {
        when (fileItemAction) {
            FileItemAction.CLICK -> {
                if (file.isImage) {
                    openFileInGallery(file.fileModel)
                } else {
                    openFileInExternal(file.fileModel)
                }
            }
            FileItemAction.OPEN -> openFileInGallery(file.fileModel)
            FileItemAction.OPEN_EXTERNAL -> openFileInExternal(file.fileModel)
            FileItemAction.UNLOAD -> unloadFile(file.fileModel)
            FileItemAction.DELETE -> deleteFile(file.fileModel)
        }
    }

    override fun onNavigateBack() {
        galleryFileModel = null
        state = state.copy(
            galleryBitmap = null,
            isBackHandlerEnabled = false
        )
    }

    private fun openFileInGallery(fileModel: FileModel) = viewModelScope.launch {
        state = state.copy(inProgress = true)
        val fileIO = storageInteractor.loadFile(fileModel)
        state = state.copy(inProgress = false)

        if (fileIO != null) {
            val bitmap = decodeAsBitmap(fileIO)
            if (bitmap != null) {
                galleryFileModel = fileModel
                state = state.copy(
                    galleryBitmap = bitmap,
                    isBackHandlerEnabled = true
                )
            } else {
                intentUtils.openFile(
                    file = fileIO,
                    mimeType = fileModel.mimeType
                )
            }
        } else {
            showError()
        }
    }

    private fun openFileInExternal(fileModel: FileModel) = viewModelScope.launch {
        state = state.copy(inProgress = true)
        val fileIO = storageInteractor.loadFile(fileModel)
        state = state.copy(inProgress = false)

        if (fileIO != null) {
            intentUtils.openFile(
                file = fileIO,
                mimeType = fileModel.mimeType
            )
        } else {
            showError()
        }
    }

    private fun unloadFile(fileModel: FileModel) {
        unloadFileModel = fileModel
        onFileUnload(fileModel.name)
    }

    private fun deleteFile(fileModel: FileModel) = viewModelScope.launch {
        storageInteractor.deleteFile(fileModel)
        loadFiles()
    }

    private fun findNextFile(
        files: List<FileModel>,
        currentFile: FileModel,
        iterator: (Int) -> Int,
    ): FileModel? {
        val index = files.indexOfFirst { it == currentFile }
        var currentIndex = index
        do {
            currentIndex = iterator(currentIndex)
            when {
                currentIndex < 0 -> currentIndex = files.lastIndex
                currentIndex > files.lastIndex -> currentIndex = 0
            }

            val currentItem = state.items.getOrNull(currentIndex)
            if (currentItem?.isImage == true) {
                return currentItem.fileModel
            }
        } while (currentIndex != index)

        return null
    }

    private suspend fun loadFiles() {
        state = state.copy(inProgress = true)
        files = storageInteractor.getAllFiles()
        state = state.copy(
            items = buildItems(
                files = files,
                query = state.searchQuery
            ),
            inProgress = false
        )
    }

    private suspend fun showError() {
        val message = resourceManager.getString(R.string.error)
        snackbarHostState.showSnackbar(message)
    }

    private fun buildItems(
        files: List<FileModel>?,
        query: String
    ): List<FileUIModel> {
        val queryTokens = query
            .split(' ')
            .filter { it.isNotEmpty() }

        return files.orEmpty()
            .filter { file ->
                val contains = queryTokens.any { file.name.contains(it) }
                val isEmpty = queryTokens.isEmpty()
                contains || isEmpty
            }
            .mapFiles()
    }

    private fun decodeAsBitmap(file: File): Bitmap? = try {
        file.inputStream().use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (_: Throwable) {
        null
    }

    private fun List<FileModel>.mapFiles(): List<FileUIModel> =
        map { fileModel ->
            FileUIModel(
                name = fileModel.name,
                size = formatFileSizeExact(fileModel.size),
                isImage = fileModel.mimeType == JPEG || fileModel.mimeType == PNG,
                fileModel = fileModel
            )
        }

    private fun formatFileSizeExact(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes Б"
            bytes < 1024 * 1024 -> {
                val kb = bytes / 1024.0
                String.format(Locale.US, "%.1f КБ", kb)
            }

            else -> {
                val mb = bytes / (1024.0 * 1024.0)
                String.format(Locale.US, "%.1f МБ", mb)
            }
        }
    }

    companion object {
        private const val JPEG = "image/jpeg"
        private const val PNG = "image/png"
    }
}

data class MainViewState(
    val searchQuery: String = "",
    val items: List<FileUIModel> = emptyList(),
    val galleryBitmap: Bitmap? = null,
    val inProgress: Boolean = false,
    val isBackHandlerEnabled: Boolean = false,
)