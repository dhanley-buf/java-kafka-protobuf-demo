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

### Running the Applications
Both `ProtoProducer` and `ProtoConsumer` classes contain main methods and can be run directly. They expect:
- Kafka broker at `localhost:9092`
- Confluent Schema Registry integration for protobuf producers/consumers
- Appropriate authentication credentials for schema registry

## Environment Configuration

### Required Environment Variables

Both applications require the following environment variable to be set:

- **`USER_INFO_CONFIG`**: Schema Registry authentication credentials in the format `username:password`

### Setting Environment Variables

**For development (local):**
```bash
export USER_INFO_CONFIG="your-username:your-password"
```

**For production/deployment:**
Set the environment variable through your deployment platform (Docker, Kubernetes, etc.)

**For IDE/IntelliJ:**
Set the environment variable in your run configuration.

### Security Benefits

- **No hardcoded credentials** in source code
- **Safe to commit** all code to version control
- **Environment-specific configuration** without code changes
- **Follows 12-factor app principles**

If the `USER_INFO_CONFIG` environment variable is not set, both applications will log an error and exit gracefully.