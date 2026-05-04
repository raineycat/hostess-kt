package uk.reddust.hostess

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readUShortLe
import kotlinx.io.writeUShortLe

data class PacketHeader(val type: PacketType, val bodySize: Long) {
    companion object {
        fun decode(buffer: Source): PacketHeader {
            val type = buffer.readByte()
            val size = buffer.readUShortLe()
            return PacketHeader(
                PacketType.get(type),
                (size - 3u).toLong()
            )
        }

        fun encode(header: PacketHeader, buffer: Sink) {
            buffer.writeByte(header.type.value)
            buffer.writeUShortLe((header.bodySize + 3).toUShort())
        }
    }
}
