package com.github.matheusfbosa.order

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

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

    fun listOutboxMessages(status: OutboxStatus): List<OutboxMessage> {
        return Outbox.selectAll().where { Outbox.status eq status }.map { row ->
            OutboxMessage(
                id = row[Outbox.id],
                aggregateType = row[Outbox.aggregateType],
                aggregateId = row[Outbox.aggregateId],
                type = row[Outbox.type],
                payload = row[Outbox.payload],
                createdAt = row[Outbox.createdAt].toString()
            )
        }
    }

    fun updateOutboxMessage(message: OutboxMessage, status: OutboxStatus) {
        Outbox.update({ Outbox.id eq message.id }) {
            it[Outbox.status] = status
        }
    }
}
