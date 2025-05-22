#!/bin/bash

echo "Creating Kafka topics..."

KAFKA_BIN="/opt/bitnami/kafka/bin/kafka-topics.sh"
BROKER="kafka:9092"

$KAFKA_BIN --create --if-not-exists --bootstrap-server $BROKER --replication-factor 1 --partitions 1 --topic event-created
$KAFKA_BIN --create --if-not-exists --bootstrap-server $BROKER --replication-factor 1 --partitions 1 --topic user-registered

echo "Kafka topics created"