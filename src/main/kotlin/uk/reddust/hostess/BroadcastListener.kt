package uk.reddust.hostess

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.io.Buffer
import uk.reddust.hostess.packets.ClientAnnouncePacket
import uk.reddust.hostess.packets.ServerAcceptPacket

class BroadcastListener(val socket: BoundDatagramSocket) {
    companion object {
        val logger = KotlinLogging.logger {  }
        val selectorManager = SelectorManager(Dispatchers.IO)

        suspend fun create(addr: String): BroadcastListener {
            val socket = aSocket(selectorManager).udp().bind(addr, 3309) {
                this.broadcast = true
            }
            return BroadcastListener(socket)
        }
    }

    suspend fun receive() {
        val datagram = socket.receive()
        val packet = Packet.read(datagram.packet)
        logger.debug { "datagram from ${datagram.address}: $packet" }

        if(packet is ClientAnnouncePacket) {
            logger.debug { "client announce: ${packet.text}" }
            val sink = Buffer()
            Packet.write(ServerAcceptPacket(), sink)

            val target = datagram.address.toJavaAddress().address
            val socket = aSocket(selectorManager).udp().bind {  }
            socket.send(Datagram(sink,InetSocketAddress(target, 3310)))
            logger.debug { "replied with server accept" }
        }
    }
}