package uk.reddust.hostess

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers

class HostessServer(val socket: ServerSocket) {
    suspend fun accept(): ClientConn {
        val conn = socket.accept()
        logger.debug { "accepted: $conn" }
        return ClientConn(conn)
    }

    companion object {
        val logger = KotlinLogging.logger {  }
        val selectorManager = SelectorManager(Dispatchers.IO)

        suspend fun create(addr: String): HostessServer {
            val socket = aSocket(selectorManager).tcp().bind(addr, 8675)
            return HostessServer(socket)
        }
    }
}