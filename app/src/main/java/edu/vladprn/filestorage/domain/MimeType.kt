package edu.vladprn.filestorage.domain

object MimeType {
    const val JPEG = "image/jpeg"
    const val PNG = "image/png"
    const val ZIP = "application/zip"

    fun isImage(mimeType: String): Boolean =
        mimeType == JPEG || mimeType == PNG
}