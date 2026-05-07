package uk.reddust.hostess.packets

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import uk.reddust.hostess.*

class DirectoryGetFilesResponsePacket : Packet {
    var clientHandle = 0
    var errorKind = ErrorKind.EpicFail
    var files = listOf<String>()

    override fun encode(buffer: Sink): PacketType {
        buffer.write7BitInt(clientHandle)
        buffer.write7BitInt(errorKind.ordinal)

        buffer.write7BitInt(files.count())
        files.forEach {
            buffer.write7BitInt(it.length)
            buffer.writeString(it)
        }

        return PacketType.DirectoryGetFilesResponse
    }

    override fun decode(header: PacketHeader, buffer: Source) {
        clientHandle = buffer.read7BitInt()
        errorKind = ErrorKind.get(buffer.read7BitInt())

        val fileCount = buffer.read7BitInt()
        for (i in 0..<fileCount) {
            val length = buffer.read7BitInt()
            files += buffer.readString(length.toLong())
        }
    }
}