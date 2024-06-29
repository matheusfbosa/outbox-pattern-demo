# outbox-pattern-demo

This repository demonstrates the implementation of the Outbox Pattern using two microservices: `order` and `shipment`.

Both services are built with [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/). The `order` service also utilizes [Exposed ORM](https://jetbrains.github.io/Exposed/home.html) to interact with a PostgreSQL database named `order_db`.

![Outbox Pattern](/docs/outbox-pattern.png)

- [Microservices.io](https://microservices.io/patterns/data/transactional-outbox.html)

## Microservices

### `order`
- A REST API to create and publish orders using the Outbox Pattern.

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

### Register Debezium Kafka connector

```sh
curl -X POST http://localhost:8083/connectors/ \
-H "Content-Type:application/json" \
-d @config/orders-debezium-kafka-connector.json
```

### Docker Compose
The `docker-compose.yml` file sets up the necessary infrastructure:
- PostgreSQL and pgAdmin.
- Kafka, Kafka UI, and Zookeeper for message brokering.
- Debezium for capturing database changes (Part 2).

## Usage

### Create a new order
```sh
curl -X POST http://localhost:8080/v1/orders \
-H "Content-Type: application/json" \
-d '{"customerId":"1", "item":"foo", "quantity":2, "totalPrice":1000}'
```

### `order` logs
```
Order created: f5a6d4c8-8b03-4743-b107-25bc69fa4f4d
```

### `shipment` logs (`Received order: <key> -> <value>` format)

- Part 1:
```
Received order: f5a6d4c8-8b03-4743-b107-25bc69fa4f4d -> {"orderId":"f5a6d4c8-8b03-4743-b107-25bc69fa4f4d","customerId":"1","item":"foo","quantity":2,"totalPrice":1000,"status":"CREATED","orderDate":"2024-06-29T16:21:09.897202"}
```

- Part 2:
```json
Received order:
{
   "schema":{
      "type":"struct",
      "fields":[
         {
            "type":"string",
            "optional":false,
            "field":"id"
         }
      ],
      "optional":false,
      "name":"orders.public.outbox.Key"
   },
   "payload":{
      "id":"032c278f-3334-47c0-9e1a-6838bdeb51e4"
   }
}
"->"
{
   "schema":{
      "type":"struct",
      "fields":[
         {
            "type":"struct",
            "fields":[
               {
                  "type":"string",
                  "optional":false,
                  "field":"id"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"aggregate_type"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"aggregate_id"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"type"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"payload"
               },
               {
                  "type":"string",
                  "optional":false,
                  "default":"PENDING",
                  "field":"status"
               },
               {
                  "type":"int64",
                  "optional":false,
                  "name":"io.debezium.time.MicroTimestamp",
                  "version":1,
                  "default":1719677803987000,
                  "field":"created_at"
               }
            ],
            "optional":true,
            "name":"orders.public.outbox.Value",
            "field":"before"
         },
         {
            "type":"struct",
            "fields":[
               {
                  "type":"string",
                  "optional":false,
                  "field":"id"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"aggregate_type"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"aggregate_id"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"type"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"payload"
               },
               {
                  "type":"string",
                  "optional":false,
                  "default":"PENDING",
                  "field":"status"
               },
               {
                  "type":"int64",
                  "optional":false,
                  "name":"io.debezium.time.MicroTimestamp",
                  "version":1,
                  "default":1719677803987000,
                  "field":"created_at"
               }
            ],
            "optional":true,
            "name":"orders.public.outbox.Value",
            "field":"after"
         },
         {
            "type":"struct",
            "fields":[
               {
                  "type":"string",
                  "optional":false,
                  "field":"version"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"connector"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"name"
               },
               {
                  "type":"int64",
                  "optional":false,
                  "field":"ts_ms"
               },
               {
                  "type":"string",
                  "optional":true,
                  "name":"io.debezium.data.Enum",
                  "version":1,
                  "parameters":{
                     "allowed":"true,last,false,incremental"
                  },
                  "default":"false",
                  "field":"snapshot"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"db"
               },
               {
                  "type":"string",
                  "optional":true,
                  "field":"sequence"
               },
               {
                  "type":"int64",
                  "optional":true,
                  "field":"ts_us"
               },
               {
                  "type":"int64",
                  "optional":true,
                  "field":"ts_ns"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"schema"
               },
               {
                  "type":"string",
                  "optional":false,
                  "field":"table"
               },
               {
                  "type":"int64",
                  "optional":true,
                  "field":"txId"
               },
               {
                  "type":"int64",
                  "optional":true,
                  "field":"lsn"
               },
               {
                  "type":"int64",
                  "optional":true,
                  "field":"xmin"
               }
            ],
            "optional":false,
            "name":"io.debezium.connector.postgresql.Source",
            "field":"source"
         },
         {
            "type":"string",
            "optional":false,
            "field":"op"
         },
         {
            "type":"int64",
            "optional":true,
            "field":"ts_ms"
         },
         {
            "type":"int64",
            "optional":true,
            "field":"ts_us"
         },
         {
            "type":"int64",
            "optional":true,
            "field":"ts_ns"
         },
         {
            "type":"struct",
            "fields":[
               {
                  "type":"string",
                  "optional":false,
                  "field":"id"
               },
               {
                  "type":"int64",
                  "optional":false,
                  "field":"total_order"
               },
               {
                  "type":"int64",
                  "optional":false,
                  "field":"data_collection_order"
               }
            ],
            "optional":true,
            "name":"event.block",
            "version":1,
            "field":"transaction"
         }
      ],
      "optional":false,
      "name":"orders.public.outbox.Envelope",
      "version":2
   },
   "payload":{
      "before":null,
      "after":{
         "id":"032c278f-3334-47c0-9e1a-6838bdeb51e4",
         "aggregate_type":"Order",
         "aggregate_id":"032c278f-3334-47c0-9e1a-6838bdeb51e4",
         "type":"CREATED",
         "payload":"{\"orderId\":\"032c278f-3334-47c0-9e1a-6838bdeb51e4\",\"customerId\":\"1\",\"item\":\"foo\",\"quantity\":2,\"totalPrice\":1000,\"status\":\"CREATED\",\"orderDate\":\"2024-06-29T16:56:12.430407\"}",
         "status":"PENDING",
         "created_at":1719680162823282
      },
      "source":{
         "version":"2.6.2.Final",
         "connector":"postgresql",
         "name":"orders",
         "ts_ms":1719690972831,
         "snapshot":"false",
         "db":"order_db",
         "sequence":"[null,\"26752656\"]",
         "ts_us":1719690972831867,
         "ts_ns":1719690972831867000,
         "schema":"public",
         "table":"outbox",
         "txId":752,
         "lsn":26752656,
         "xmin":null
      },
      "op":"c",
      "ts_ms":1719690973574,
      "ts_us":1719690973574838,
      "ts_ns":1719690973574838000,
      "transaction":null
   }
}
```
