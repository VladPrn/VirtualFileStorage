package edu.vladprn.filestorage.domain.model

data class FileModel(
    val id: Long = 0,
    val name: String,
    val mimeType: String,
    val size: Long,
    val addresses: List<Int>? = null,
)