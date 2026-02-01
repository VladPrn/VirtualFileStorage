package edu.vladprn.filestorage.data.mapper

import edu.vladprn.filestorage.data.database.entity.FileEntity
import edu.vladprn.filestorage.domain.model.FileModel

class FileEntityMapper {

    fun mapToEntity(fileModel: FileModel): FileEntity = FileEntity(
        id = fileModel.id,
        name = fileModel.name,
        mimeType = fileModel.mimeType,
        size = fileModel.size
    )

    fun mapFromEntity(fileEntity: FileEntity): FileModel = FileModel(
        id = fileEntity.id,
        name = fileEntity.name,
        mimeType = fileEntity.mimeType,
        size = fileEntity.size
    )

    fun mapFromEntities(list: List<FileEntity>): List<FileModel> =
        list.map { mapFromEntity(it) }
}