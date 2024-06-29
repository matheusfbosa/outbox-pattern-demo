package com.github.matheusfbosa.order

import org.jetbrains.exposed.sql.transactions.transaction

class Service(
    private val repository: Repository
) {
    fun createOrder(order: Order): String {
        with(order) {
            require(totalPrice >= 0) { "Total price must be positive" }
            require(quantity > 0) { "Quantity greater than zero" }
            require(customerId.isNotBlank()) { "Customer ID must not be blank" }
            require(item.isNotBlank()) { "Item must not be blank" }
        }
        transaction {
            repository.saveOrder(order)
            repository.saveOutboxMessage(order)
        }
        println("Order created: ${order.orderId}")
        return order.orderId
    }
}
