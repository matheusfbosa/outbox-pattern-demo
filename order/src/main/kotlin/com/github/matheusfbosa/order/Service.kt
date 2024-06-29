package com.github.matheusfbosa.order

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.jetbrains.exposed.sql.transactions.transaction

class Service(
    private val repository: Repository,
    private val producer: KafkaProducer<String, String>,
    private val topic: String
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

    fun processOutboxMessages() {
        transaction {
            repository.listOutboxMessages(status = OutboxStatus.PENDING).forEach { message ->
                val record = ProducerRecord(topic, message.id, message.payload)
                println("Publishing outbox message: $record")
                producer.send(record)
                repository.updateOutboxMessage(message, status = OutboxStatus.SENT)
            }
        }
    }
}
