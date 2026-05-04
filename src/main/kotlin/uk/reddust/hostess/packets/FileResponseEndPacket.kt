package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import uk.reddust.hostess.*

class FileResponseEndPacket : Packet {
    var clientHandle = 0

    override fun encode(buffer: Sink): PacketType {
        buffer.write7BitInt(clientHandle)
        return PacketType.FileResponseBegin
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        clientHandle = buffer.read7BitInt()
    }
}