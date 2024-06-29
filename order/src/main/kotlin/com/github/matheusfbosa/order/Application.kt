package com.github.matheusfbosa.order

import io.ktor.server.application.Application
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    configureDatabase()
    configureSerialization()

    val bootstrapServers = environment.config.property("kafka.bootstrap-servers").getString()
    val ordersTopic = environment.config.property("kafka.topic").getString()

    val kafkaProducer = KafkaProducer<String, String>(
        mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        )
    )

    val service = Service(repository = Repository(), producer = kafkaProducer, topic = ordersTopic)
    configureRouting(service)

    launch {
        while (true) {
            delay(duration = Duration.ofSeconds(5))
            service.processOutboxMessages()
        }
    }
}
