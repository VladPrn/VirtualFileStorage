package edu.vladprn.filestorage.data.mapper

import edu.vladprn.filestorage.data.database.entity.FileEntity
import edu.vladprn.filestorage.data.database.entity.FileWithAddressesEntity
import edu.vladprn.filestorage.domain.model.FileModel

class FileEntityMapper(
    private val addressEntityMapper: AddressEntityMapper
) {

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

    fun mapFromEntity(
        fileWithAddressesEntity: FileWithAddressesEntity
    ): FileModel = FileModel(
        id = fileWithAddressesEntity.file.id,
        name = fileWithAddressesEntity.file.name,
        mimeType = fileWithAddressesEntity.file.mimeType,
        size = fileWithAddressesEntity.file.size,
        addresses = addressEntityMapper.mapFromEntity(fileWithAddressesEntity.addresses)
    )

    fun mapFromEntities(list: List<FileEntity>): List<FileModel> =
        list.map { mapFromEntity(it) }

    fun mapFromAddressesEntities(list: List<FileWithAddressesEntity>): List<FileModel> =
        list.map { mapFromEntity(it) }
}