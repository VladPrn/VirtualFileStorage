package edu.vladprn.filestorage.domain.interactor

class AvailableAddressesBitMask {

    private val data = mutableListOf<ULong>()
    private val sizeBits = ULong.SIZE_BITS

    fun setValue(index: Int, value: Boolean) {
        val listIndex = index / sizeBits
        val bitIndex = index % sizeBits

        while (listIndex >= data.size) {
            data.add(0UL)
        }

        if (value) {
            data[listIndex] = data[listIndex] or (1UL shl bitIndex)
        } else {
            data[listIndex] = data[listIndex] and (1UL shl bitIndex).inv()
        }
    }

    fun getValue(index: Int): Boolean {
        val listIndex = index / sizeBits
        val bitIndex = index % sizeBits

        if (listIndex >= data.size) {
            return false
        }

        return (data[listIndex] and (1UL shl bitIndex)) != 0UL
    }

    fun findAvailableAddresses(required: Int): List<Int> {
        val result = mutableListOf<Int>()
        var currentIterator = 0

        while (true) {
            while (currentIterator < data.size && data[currentIterator] == ULong.MAX_VALUE) {
                currentIterator++
            }
            repeat(sizeBits) { i ->
                val index = currentIterator * sizeBits + i
                if (!getValue(index)) {
                    result.add(index)
                }
                if (result.size == required) return result
            }
            currentIterator++
        }
    }
}