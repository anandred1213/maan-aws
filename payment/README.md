# Payment Microservice

## Overview
Production-ready payment microservice for e-commerce platform handling payment processing via Kafka events.

## Prerequisites
- Java 21
- Maven 3.6+
- Kafka (Zookeeper + Kafka Broker)
- Docker & Docker Compose (for containerized deployment)

## Features
- ✅ Kafka event-driven architecture
- ✅ Global exception handling
- ✅ Health checks and monitoring (Spring Actuator)
- ✅ Structured logging with Logback
- ✅ Secure CORS configuration
- ✅ Docker support with health checks
- ✅ Production and development profiles
- ✅ Resilience patterns with Resilience4j

## Setup

### Local Development

1. Start Kafka:
```bash
docker-compose up -d zookeeper kafka
```

2. Build:
```bash
mvn clean install
```

3. Run:
```bash
mvn spring-boot:run
```

### Production Deployment

1. Create `.env` file:
```bash
cp .env.example .env
```

2. Update `.env` with production values

3. Build:
```bash
mvn clean package -DskipTests
```

4. Run with Docker:
```bash
docker-compose up -d payment
```

## Kafka Integration

### Consumed Topics
- `order-topic` - Receives order events for payment processing

### Consumer Group
- `payment-group`

## Monitoring Endpoints

- `GET /actuator/health` - Health check
- `GET /actuator/info` - Application info
- `GET /actuator/metrics` - Metrics
- `GET /actuator/prometheus` - Prometheus metrics

## Configuration Profiles

- `local` - Local development
- `docker` - Docker environment
- `production` - Production deployment

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| KAFKA_BOOTSTRAP_SERVERS | Kafka broker URL | localhost:9092 |
| ALLOWED_ORIGINS | CORS allowed origins | http://localhost:3000 |
| SPRING_PROFILES_ACTIVE | Active profile | local |

## Port
8082

## Logging

Logs are stored in:
- Console output (development)
- `./logs/payment-microservice.log` (local)
- `/var/log/payment-microservice.log` (production)
- Rolling file policy: 10MB per file, 30 days retention

## Error Handling

- Kafka exceptions are logged and handled gracefully
- Invalid messages are logged with warnings
- All errors return structured JSON responses

## Production Best Practices

- ✅ Structured logging with SLF4J
- ✅ Health checks for monitoring
- ✅ Non-root Docker user
- ✅ JVM optimization flags
- ✅ Environment-based configuration
- ✅ CORS security
- ✅ Log rotation
