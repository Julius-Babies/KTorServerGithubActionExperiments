package es.jvbabi

import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.rabbitMQ
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection
import java.sql.DriverManager
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*

fun Application.configureFrameworks() {
    install(RabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        defaultConnectionName = "default-connection"
        dispatcherThreadPollSize = 4
        tlsEnabled = false
    }

    rabbitmq {
        queueBind {
            queue = "dlq"
            exchange = "dlx"
            routingKey = "dlq-dlx"
            exchangeDeclare {
                exchange = "dlx"
                type = "direct"
            }
            queueDeclare {
                queue = "dlq"
                durable = true
            }
        }
    }

    rabbitmq {
        queueBind {
            queue = "test-queue"
            exchange = "test-exchange"
            routingKey = "test-routing-key"
            exchangeDeclare {
                exchange = "test-exchange"
                type = "direct"
            }
            queueDeclare {
                queue = "test-queue"
                arguments = mapOf(
                    "x-dead-letter-exchange" to "dlx",
                    "x-dead-letter-routing-key" to "dlq-dlx"
                )
            }
        }
    }

    routing {
        get("/rabbitmq") {
            rabbitmq {
                basicPublish {
                    exchange = "test-exchange"
                    routingKey = "test-routing-key"
                    message { "Hello Ktor!" }
                }
            }
            call.respondText("Hello RabbitMQ!")
        }


        rabbitmq {
            basicConsume {
                autoAck = true
                queue = "test-queue"
                dispatcher = Dispatchers.rabbitMQ
                coroutinePollSize = 100
                deliverCallback<String> { tag, message ->
                    log.info("Received message: $message")
                }
            }
        }
    }
}
