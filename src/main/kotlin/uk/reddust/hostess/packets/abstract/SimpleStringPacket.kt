package uk.reddust.hostess.packets.abstract

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import uk.reddust.hostess.Packet
import uk.reddust.hostess.PacketHeader
import uk.reddust.hostess.PacketType

open class SimpleStringPacket(val packetType: PacketType) : Packet {
    var text = ""

    override fun encode(buffer: Sink): PacketType {
        buffer.writeString(text)
        return packetType
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        text = buffer.readString(header.bodySize)
    }
}