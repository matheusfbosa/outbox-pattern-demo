package com.github.matheusfbosa.order

import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    configureDatabase()
    configureSerialization()

    val service = Service(repository = Repository())
    configureRouting(service)
}
