package edu.vladprn.filestorage.data.mapper

import edu.vladprn.filestorage.data.database.entity.AddressEntity
import edu.vladprn.filestorage.domain.model.FileModel

class AddressEntityMapper {

    fun mapToEntity(fileModel: FileModel): List<AddressEntity>? =
        fileModel.addresses?.map { address ->
            AddressEntity(
                fileId = fileModel.id,
                address = address
            )
        }

    fun mapFromEntity(addresses: List<AddressEntity>): List<Int> =
        addresses.map { it.address }
}