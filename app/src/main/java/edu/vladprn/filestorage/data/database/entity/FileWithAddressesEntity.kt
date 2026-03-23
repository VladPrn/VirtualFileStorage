package edu.vladprn.filestorage.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FileWithAddressesEntity(
    @Embedded
    val file: FileEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "fileId"
    )
    val addresses: List<AddressEntity>
)