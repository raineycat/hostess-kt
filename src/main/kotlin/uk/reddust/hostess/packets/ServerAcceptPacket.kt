package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import uk.reddust.hostess.Packet
import uk.reddust.hostess.PacketHeader
import uk.reddust.hostess.PacketType

class ServerAcceptPacket : Packet {
    override fun encode(buffer: Sink): PacketType {
        return PacketType.ServerAccept
    }

    override fun decode(header: PacketHeader, buffer: Source) {

    }
}
