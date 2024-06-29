# outbox-pattern-demo

This repository demonstrates the implementation of the Outbox Pattern using two microservices: `order` and `shipment`.

Both services are built with [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/). The `order` service also utilizes [Exposed ORM](https://jetbrains.github.io/Exposed/home.html) to interact with a PostgreSQL database named `order_db`.

![Outbox Pattern](/docs/outbox-pattern.png)

- [Microservices.io](https://microservices.io/patterns/data/transactional-outbox.html)

## Microservices

### `order`
- A REST API to create and publish orders the Outbox Pattern.

### `shipment`
- A Kafka consumer that listens for new orders.

## Project

### Part 1: Polling Publisher Pattern (branch `feat/polling-publisher-pattern`)
The `order` service manually publishes new orders to a Kafka topic in a scheduled job that runs every 5 seconds, checking for orders that haven't been published yet.

### Part 2: Transaction Log Tailing Pattern (branch `feat/transaction-log-tailing-pattern`)
The manual job is replaced by a Debezium connector that listens to the PostgreSQL database changes and publishes new orders to the Kafka topic automatically.

## Setup

```sh
git clone https://github.com/matheusfbosa/outbox-pattern-demo.git
cd outbox-pattern-demo
git checkout feat/polling-publisher-pattern # feat/transaction-log-tailing-pattern
docker-compose up -d
```

### Docker Compose
The `docker-compose.yml` file sets up the necessary infrastructure:
- PostgreSQL and pgAdmin.
- Kafka, Kafka UI, and Zookeeper for message brokering.
- Debezium for capturing database changes (Part 2).

## Usage

- Create a new order:
```sh
curl -X POST http://localhost:8080/v1/orders \
-H "Content-Type: application/json" \
-d '{"customerId":"1", "item":"foo", "quantity":2, "totalPrice":1000}'
```

- `order` logs:
```
Order created: f5a6d4c8-8b03-4743-b107-25bc69fa4f4d
```

- `shipment` logs:
```
Received order: f5a6d4c8-8b03-4743-b107-25bc69fa4f4d -> {"orderId":"f5a6d4c8-8b03-4743-b107-25bc69fa4f4d","customerId":"1","item":"foo","quantity":2,"totalPrice":1000,"status":"CREATED","orderDate":"2024-06-29T16:21:09.897202"}
```
