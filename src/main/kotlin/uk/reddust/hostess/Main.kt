package uk.reddust.hostess

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun main() {
    val logger = KotlinLogging.logger {  }
    logger.info { "Setting up Hostess server" }

    val listener = BroadcastListener.create("0.0.0.0")
    val server = HostessServer.create("0.0.0.0")

    withContext(Dispatchers.Default) {
        this.launch {
            while(true) {
                listener.receive()
            }
        }

        this.launch {
            while(true) {
                val client = server.accept()
                this.launch { client.handle() }
            }
        }

        logger.debug { "started listening" }
    }
}