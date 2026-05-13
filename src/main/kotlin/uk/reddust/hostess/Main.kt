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
    val web = WebInterface(8080)

    withContext(Dispatchers.Default) {
        launch {
            while(true) {
                listener.receive()
            }
        }

        launch {
            while(true) {
                val client = server.accept()
                web.registerClient(client)
                launch {
                    client.handle()
                    web.unregisterClient(client)
                }
            }
        }

        launch {
            web.start()
        }

        logger.debug { "started listening" }
    }
}