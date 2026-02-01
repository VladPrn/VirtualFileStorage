package edu.vladprn.filestorage.domain.interactor

import edu.vladprn.filestorage.domain.Constants
import edu.vladprn.filestorage.domain.model.FileModel

class StorageAllocator {

    private val bitMask = AvailableAddressesBitMask()

    fun fillAllocatedAddresses(addresses: List<Int>) {
        addresses.forEach { address ->
            bitMask.setValue(index = address, value = true)
        }
    }

    fun allocateAddresses(file: FileModel): List<Int> {
        val segmentCount = (file.size - 1) / Constants.SEGMENT_SIZE + 1
        return bitMask.findAvailableAddresses(required = segmentCount.toInt())
    }
}