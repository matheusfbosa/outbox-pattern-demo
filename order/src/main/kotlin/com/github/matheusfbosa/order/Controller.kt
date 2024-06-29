package com.github.matheusfbosa.order

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Routing.orderController(service: Service) {
    get("/health") {
        call.respond(HttpStatusCode.OK, "OK")
    }
    route("/v1/orders") {
        createOrder(service)
    }
}

private fun Route.createOrder(service: Service) {
    post {
        val createOrder = call.receive<CreateOrderRequestDTO>()
        val orderId = service.createOrder(order = createOrder.toDomain())
        call.respond(message = CreateOrderResponseDTO(orderId), status = HttpStatusCode.Accepted)
    }
}
