package com.github.matheusfbosa.order

import java.time.LocalDateTime
import java.util.UUID

fun CreateOrderRequestDTO.toDomain() = Order(
    orderId = UUID.randomUUID().toString(),
    customerId = this.customerId,
    item = this.item,
    quantity = this.quantity,
    totalPrice = this.totalPrice,
    status = OrderStatus.CREATED,
    orderDate = LocalDateTime.now().toString()
)
