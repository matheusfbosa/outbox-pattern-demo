package com.github.matheusfbosa.order

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert

class Repository {
    fun saveOrder(order: Order) {
        Orders.insert {
            it[orderId] = order.orderId
            it[customerId] = order.customerId
            it[item] = order.item
            it[quantity] = order.quantity
            it[totalPrice] = order.totalPrice
            it[status] = order.status
        }
    }

    fun saveOutboxMessage(order: Order) {
        Outbox.insert {
            it[id] = order.orderId
            it[aggregateType] = Order::class.simpleName!!
            it[aggregateId] = order.orderId
            it[type] = order.status.name
            it[payload] = Json.encodeToString(order)
            it[status] = OutboxStatus.PENDING
        }
    }
}
