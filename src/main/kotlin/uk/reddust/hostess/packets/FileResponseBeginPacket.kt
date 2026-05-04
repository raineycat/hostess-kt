package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import uk.reddust.hostess.*

class FileResponseBeginPacket : Packet {
    var clientHandle = 0
    var fileSize = 0

    override fun encode(buffer: Sink): PacketType {
        buffer.write7BitInt(clientHandle)
        buffer.write7BitInt(fileSize)
        return PacketType.FileResponseBegin
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        clientHandle = buffer.read7BitInt()
        fileSize = buffer.read7BitInt()
    }
}