package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import uk.reddust.hostess.*

class FileResponseFailurePacket : Packet {
    var clientHandle = 0
    var errorKind = ErrorKind.EpicFail

    override fun encode(buffer: Sink): PacketType {
        buffer.write7BitInt(clientHandle)
        buffer.write7BitInt(errorKind.ordinal)
        return PacketType.FileRequestBlocking
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        clientHandle = buffer.read7BitInt()
        errorKind = ErrorKind.get(buffer.read7BitInt())
    }
}