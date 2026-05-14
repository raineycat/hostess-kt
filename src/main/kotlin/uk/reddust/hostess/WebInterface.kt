package uk.reddust.hostess

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.server.velocity.Velocity
import io.ktor.server.velocity.respondTemplate
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import java.util.*

class WebInterface(val port: Int) {
    val logger = KotlinLogging.logger {  }
    private var clients = listOf<ClientConn>()

    fun start() {
        embeddedServer(Netty, port = port) {
            install(SSE)

            install(StatusPages) {
                exception<Throwable> { call, cause ->
                    if (cause is NotFoundException) {
                        call.respondTemplate("templates/404.html.vsl", mutableMapOf("msg" to cause.localizedMessage))
                        return@exception
                    }

                    call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
                    throw cause
                }
            }

            install(Velocity) {
                setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
                setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
            }

            routing {
                route("/clients") {
                    clientRoutes()
                }

                get("/pico") {
                    call.respondResource("vendor/pico.amber.min.css")
                }

                get("/contentScript") {
                    call.respondResource("content.js")
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
            call.respondTemplate("templates/list.html.vsl", mutableMapOf("clients" to clients))
        }

        route("/{id}") {
            fun RoutingContext.getClient(): ClientConn {
                val id = call.parameters["id"] ?: throw NotFoundException("No client ID supplied")
                val uuid = runCatching {
                    UUID.fromString(id)
                }.getOrElse { throw NotFoundException("Invalid client ID format") }

                return clients.find { it.uuid == uuid } ?: throw NotFoundException("The given client ID doesn't exist")
            }

            get {
                call.respondTemplate("templates/client.html.vsl", mutableMapOf("client" to getClient()))
            }

            get("/log") {
                call.respondTemplate("templates/log.html.vsl", mutableMapOf("client" to getClient()))
            }

            post("/lua") {
                val formBody = call.receiveParameters()
                getClient().sendLua(formBody["code"].toString())
                call.respondRedirect("/clients/${getClient().uuid}")
            }
        }
    }
}