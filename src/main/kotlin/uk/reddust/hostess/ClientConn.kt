package uk.reddust.hostess

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import io.ktor.util.cio.readChannel
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.isActive
import kotlinx.io.Buffer
import kotlinx.io.EOFException
import uk.reddust.hostess.packets.*
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.listDirectoryEntries

class ClientConn(val socket: Socket) {
    val uuid: UUID = UUID.randomUUID()
    var name = uuid.toString()
    val logger = KotlinLogging.logger {  }
    val writer = socket.openWriteChannel(true)
    var logHistory = listOf<String>()

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

    suspend fun sendLua(code: String) {
        logger.info { "[$name] exec: $code" }
        reply(::LuaCommandPacket) {
            this.text = code
        }
    }

    private suspend fun <TPacket: Packet> reply(ctor: () -> TPacket, builder: TPacket.() -> Unit) {
        val packet = ctor()
        packet.builder()

        val buffer = Buffer()
        Packet.write(packet, buffer)

        logger.trace { "[$name] sending ${buffer.size} bytes" }
        writer.writeBuffer(buffer.build())
    }

    private suspend fun process(packet: Packet) {
        when (packet) {
            is DebugLogPacket -> {
                logger.info { "[$name] [GameLog] ${packet.text}" }
                logHistory += packet.text
            }

            is SetClientNamePacket ->  {
                logger.info { "[$name] rename to '${packet.text}'" }
                name = packet.text
            }

            is FileRequestBlockingPacket -> {
                val path = Path(packet.fileName)
                logger.info { "[$name] request file blocking: $path (${packet.clientHandle})" }

                if(!path.exists()) {
                    reply(::FileResponseFailurePacket) {
                        this.clientHandle = packet.clientHandle
                        this.errorKind = ErrorKind.FileOpenFail
                    }
                    return
                }

                sendFile(packet.clientHandle, Path(packet.fileName))
            }

            is FileExistsRequestPacket -> {
                val path = Path(packet.fileName)
                logger.info { "[$name] check file exists: $path (${packet.clientHandle})" }

                reply(::FileExistsResponsePacket) {
                    this.clientHandle = packet.clientHandle
                    this.errorKind = ErrorKind.Success
                    this.exists = path.exists()
                }
            }

            is DirectoryGetFilesPacket -> {
                val path = Path(packet.path)
                logger.info { "[$name] enumerate dir: $path (${packet.clientHandle})" }

                val entries = path.listDirectoryEntries(packet.searchPattern)
                reply(::DirectoryGetFilesResponsePacket) {
                    this.clientHandle = packet.clientHandle
                    this.errorKind = ErrorKind.Success
                    this.files = entries.map { it.toString() }
                }
            }

            else -> {
                logger.warn { "[$name] Unhandled packet: $packet" }
            }
        }
    }

    private suspend fun sendFile(clientHandle: Int, path: Path) {
        reply(::FileResponseBeginPacket) {
            this.clientHandle = clientHandle
            this.fileSize = path.fileSize().toInt()
        }

        val reader = path.readChannel()
        val chunkSize = 1024L
        var position = 0L
        var read = chunkSize

        while(read == chunkSize) {
            val data = reader.readBuffer(chunkSize.toInt())
            read = data.size

            logger.trace { "[$name] read $read bytes from file $clientHandle" }
            reply(::FileResponseDataPacket) {
                this.clientHandle = clientHandle
                this.fileOffset = position.toInt()
                this.data = data.readBytes()
            }

            position += read
            logger.trace { "[$name] file $clientHandle position is now $position (+$read)" }
        }

        reply(::FileResponseEndPacket) {
            this.clientHandle = clientHandle
        }
    }
}
