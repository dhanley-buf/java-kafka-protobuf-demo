# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

This is a Maven-based Java project with the following standard commands:

- `mvn compile` - Compile the source code
- `mvn test` - Run unit tests
- `mvn package` - Create JAR file
- `mvn clean` - Clean build artifacts
- `mvn install` - Install to local repository

## Architecture Overview

This project demonstrates Kafka producers using Buf-generated Protocol Buffers in Java. The codebase is structured as follows:

### Core Structure
- **Generated Code**: `src/main/gen/` - Auto-generated Java classes from Protocol Buffers via Buf
- **Source Code**: `src/main/java/build/buf/bufstream/example/` - Main application code
- **Proto Definitions**: `src/main/proto/bufstream/example/` - Protocol Buffer schema definitions

### Key Components
- **Invoice Schema**: Defined in `Invoice.proto` with `Invoice` and `LineItem` messages
- **Producer Example**: `ProtoProducer` - Protobuf-serialized messages with Confluent Schema Registry integration, generates randomized invoice data
- **Consumer Example**: `ProtoConsumer` - Protobuf message consumer

### Protocol Buffer Generation
- Uses Buf CLI for schema management and code generation
- Configuration in `buf.yaml` and `buf.gen.yaml`
- Generates Java classes with `build.buf` package prefix
- Integrates with Confluent Schema Registry via Buf extensions

### Dependencies
- Apache Kafka 4.0.0 for messaging
- Confluent Kafka Protobuf Serializer 7.9.1 for schema registry integration
- Buf-generated protobuf dependencies from custom repository
- Java 17 target version

### Running the Producer
The `ProtoProducer` class contains a main method and can be run directly. It expects:
- Kafka broker at `localhost:9092`
- Confluent Schema Registry integration for protobuf producers
- Appropriate authentication credentials for schema registry