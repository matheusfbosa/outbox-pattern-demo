package com.github.matheusfbosa.order

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Orders : Table() {
    val orderId = varchar("order_id", 36)
    val customerId = varchar("customer_id", 36)
    val item = varchar("item", 200)
    val quantity = integer("quantity")
    val totalPrice = integer("total_price")
    val status = enumerationByName("status", 10, OrderStatus::class)
    val orderDate = datetime("order_date").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(orderId)
}

enum class OutboxStatus {
    PENDING,
    SENT
}

object Outbox : Table() {
    val id = varchar("id", 36)
    val aggregateType = varchar("aggregate_type", 255)
    val aggregateId = varchar("aggregate_id", 255)
    val type = varchar("type", 255)
    val payload = text("payload")
    val status = enumerationByName("status", 10, OutboxStatus::class).default(OutboxStatus.PENDING)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}
