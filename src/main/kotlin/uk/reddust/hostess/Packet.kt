package uk.reddust.hostess

import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source

interface Packet {
    fun encode(buffer: Sink): PacketType
    fun decode(header: PacketHeader, buffer: Source)

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun read(buffer: Source): Packet {
            val header = PacketHeader.decode(buffer)
            val inst = header.type.clazz.constructors.first().call()
            inst.decode(header, buffer)
            return inst
        }

        fun write(packet: Packet, buffer: Sink) {
            val tempSink = Buffer()
            val type = packet.encode(tempSink)
            val header = PacketHeader(type, tempSink.size)
            PacketHeader.encode(header, buffer)
            buffer.transferFrom(tempSink as Source)
        }
    }
}