package edu.vladprn.filestorage.ui.screen.main

import edu.vladprn.filestorage.domain.model.FileModel

data class FileUIModel(
    val name: String,
    val size: String,
    val isImage: Boolean,
    val fileModel: FileModel,
)