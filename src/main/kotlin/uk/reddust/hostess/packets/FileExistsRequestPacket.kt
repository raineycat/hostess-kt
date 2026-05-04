package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import uk.reddust.hostess.Packet
import uk.reddust.hostess.PacketHeader
import uk.reddust.hostess.PacketType
import uk.reddust.hostess.read7BitInt
import uk.reddust.hostess.readBool
import uk.reddust.hostess.write7BitInt
import uk.reddust.hostess.writeBool

class FileExistsRequestPacket : Packet {
    var clientHandle = 0
    var isDirectory = false
    var fileName = ""

    override fun encode(buffer: Sink): PacketType {
        buffer.write7BitInt(clientHandle)
        buffer.writeBool(isDirectory)
        buffer.writeString(fileName)
        return PacketType.FileRequestBlocking
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        clientHandle = buffer.read7BitInt()
        isDirectory = buffer.readBool()
        fileName = buffer.readString()
    }
}