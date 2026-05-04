package uk.reddust.hostess.packets

import io.ktor.utils.io.core.remaining
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import uk.reddust.hostess.*

class FileResponseDataPacket : Packet {
    var clientHandle = 0
    var fileOffset = 0
    var data = ByteArray(0)

    override fun encode(buffer: Sink): PacketType {
        buffer.write7BitInt(clientHandle)
        buffer.write7BitInt(fileOffset)
        buffer.write(data)
        return PacketType.FileResponseData
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        clientHandle = buffer.read7BitInt()
        fileOffset = buffer.read7BitInt()
        data = buffer.readByteArray(buffer.remaining.toInt())
    }
}