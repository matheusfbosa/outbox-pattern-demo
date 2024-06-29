package com.github.matheusfbosa.shipment

import io.ktor.server.application.Application
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val bootstrapServers = environment.config.property("kafka.bootstrap-servers").getString()
    val ordersTopic = environment.config.property("kafka.topic").getString()
    KafkaConsumer<String, String>(
        mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "shipment",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        )
    ).apply {
        subscribe(listOf(ordersTopic))
        while (true) {
            poll(Duration.ofSeconds(1)).forEach { record ->
                println("Received order: ${record.key()} -> ${record.value()}")
            }
        }
    }
}
