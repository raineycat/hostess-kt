package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readUInt
import kotlinx.io.writeUInt
import uk.reddust.hostess.*

class CheatKeyPacket : Packet {
    var key = KeyCombo()

    override fun encode(buffer: Sink): PacketType {
        buffer.writeUInt(key.encode())
        return PacketType.CheatKey
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        key = KeyCombo.decode(buffer.readUInt())
    }
}