package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import uk.reddust.hostess.*

class DirectoryGetFilesPacket : Packet {
    var clientHandle = 0
    var path = ""
    var searchPattern = ""

    override fun encode(buffer: Sink): PacketType {
        buffer.write7BitInt(clientHandle)

        buffer.write7BitInt(path.length)
        buffer.writeString(path)

        buffer.write7BitInt(searchPattern.length)
        buffer.writeString(searchPattern)

        return PacketType.DirectoryGetFiles
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        clientHandle = buffer.read7BitInt()

        val pathLen = buffer.read7BitInt()
        path = buffer.readString(pathLen.toLong())

        val searchPatternLen = buffer.read7BitInt()
        searchPattern = buffer.readString(searchPatternLen.toLong())
    }
}