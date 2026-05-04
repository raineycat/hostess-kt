package uk.reddust.hostess

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.isActive
import kotlinx.io.Buffer
import kotlinx.io.EOFException
import uk.reddust.hostess.packets.*

class ClientConn(val socket: Socket) {
    var name = "client"
    val logger = KotlinLogging.logger {  }

    suspend fun handle() {
        val channel = socket.openReadChannel()
        logger.debug { "[$name] open" }
        while (socket.isActive) {
            val data = try {
                channel.readPacket(3)
            } catch(_: EOFException) {
                break
            }
            if(data.remaining < 1) {
                continue
            }

            val header = PacketHeader.decode(data)
            logger.debug { "[$name] got packet $header" }
            val remaining = channel.readPacket(header.bodySize.toInt())

            val packet = header.type.clazz.constructors.first().call()
            packet.decode(header, remaining)

            process(packet)
        }
        logger.debug { "[$name] DC" }
    }

    private suspend fun reply(packet: Packet) {
        val buffer = Buffer()
        Packet.write(packet, buffer)

        val channel = socket.openWriteChannel(true)
        logger.debug { "[$name] sending ${buffer.size} bytes" }
        channel.writePacket(buffer)
    }

    private suspend fun process(packet: Packet) {
        when (packet) {
            is DebugLogPacket -> logger.info { "[$name] [GameLog] ${packet.text}" }

            is SetClientNamePacket ->  {
                logger.info { "[$name] rename to '${packet.text}'" }
                name = packet.text
            }

            is FileRequestBlockingPacket -> {
                logger.info { "[$name] request file: ${packet.fileName} (${packet.clientHandle})" }
                reply(FileResponseFailurePacket().apply {
                    this.clientHandle = packet.clientHandle
                    this.errorKind = ErrorKind.FileOpenFail
                })
            }

            is FileExistsRequestPacket -> {
                logger.info { "[$name] check file exists: ${packet.fileName} (${packet.clientHandle})" }
                reply(FileExistsResponsePacket().apply {
                    this.clientHandle = packet.clientHandle
                    this.errorKind = ErrorKind.Success
                    this.exists = false
                })
            }
        }
    }
}
