package uk.reddust.hostess

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import java.util.*

class WebInterface(val port: Int) {
    val logger = KotlinLogging.logger {  }
    private var clients = listOf<ClientConn>()

    fun start() {
        embeddedServer(Netty, port = port) {
            install(SSE)
            install(StatusPages)

            routing {
                route("/clients") {
                    clientRoutes()
                }
            }
        }.start(wait = true)
    }

    fun registerClient(c: ClientConn) {
        clients += c
    }

    fun unregisterClient(c: ClientConn) {
        clients -= c
    }

    private fun Route.clientRoutes() {
        get {
            call.respondText(clients.map { it.uuid }.joinToString("\n"))
        }

        route("/{id}") {
            fun RoutingContext.getClient(): ClientConn {
                val id = call.parameters["id"] ?: throw NotFoundException()
                val uuid = runCatching {
                    UUID.fromString(id)
                }.getOrElse { throw NotFoundException() }

                return clients.find { it.uuid == uuid } ?: throw NotFoundException()
            }

            get {
                call.respondText { getClient().name }
            }

            get("/log") {
                call.respondText { getClient().logHistory.joinToString("\n") }
            }

            post("/lua") {
                val formBody = call.receiveParameters()
                getClient().sendLua(formBody["code"].toString())
            }
        }
    }
}