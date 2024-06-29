package com.github.matheusfbosa.order

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/order_db",
        user = "postgres",
        password = "password"
    )

    transaction {
        SchemaUtils.create(Orders)
        SchemaUtils.create(Outbox)
    }
}

fun Application.configureRouting(service: Service) {
    routing {
        orderController(service)
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is IllegalArgumentException -> call.respond(
                    message = "404: ${cause.message}",
                    status = HttpStatusCode.BadRequest
                )

                else -> call.respond(message = "500: ${cause.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
