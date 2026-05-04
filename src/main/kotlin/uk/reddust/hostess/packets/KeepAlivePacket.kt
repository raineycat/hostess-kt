package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import uk.reddust.hostess.Packet
import uk.reddust.hostess.PacketHeader
import uk.reddust.hostess.PacketType

class KeepAlivePacket : Packet {
    override fun encode(buffer: Sink): PacketType {
        return PacketType.KeepAlive
    }

    override fun decode(header: PacketHeader, buffer: Source) {

    }
}
