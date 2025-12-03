#!/bin/bash

echo "Creating Kafka topics..."

KAFKA_BIN="/usr/bin/start-kafka.sh"
BROKER="kafka:9092"

$KAFKA_BIN --crete --if-not-exists --bootstrap-server $BROKER --replication-factor 1 --partitions 1 --topic event_created
$KAFKA_BIN --create --if-not-exists --bootstrap-server $BROKER --replication-factor 1 --partitions 1 --topic user_registered
$KAFKA_BIN --create --if-not-exists --bootstrap-server $BROKER --replication-factor 1 --partitions 1 --topic user_registered_to_event

echo "Kafka topics created"