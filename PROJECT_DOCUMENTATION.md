# Microservices Application Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Services](#services)
3. [Request Flow Diagrams](#request-flow-diagrams)
4. [Technology Stack](#technology-stack)
5. [Setup and Deployment](#setup-and-deployment)

---

## Architecture Overview

This is a Spring Boot microservices application with event-driven architecture using Apache Kafka for asynchronous communication.

### System Components

```
┌─────────────────┐
│   API Gateway   │ (Port: 9000)
│  (Entry Point)  │
└────────┬────────┘
         │
    ┌────┴────┐
    │ Eureka  │ (Port: 8761)
    │ Server  │ (Service Discovery)
    └────┬────┘
         │
    ┌────┴──────────────────────────┐
    │                                │
┌───▼────────┐  ┌──────────────┐  ┌▼──────────┐
│   User     │  │    Order     │  │  Payment  │
│  Service   │  │   Service    │  │  Service  │
│ (revision) │  │              │  │           │
│ Port: 8080 │  │ Port: 8081   │  │Port: 8082 │
└─────┬──────┘  └──────┬───────┘  └─────┬─────┘
      │                │                 │
      └────────┬───────┴─────────────────┘
               │
        ┌──────▼──────┐
        │    Kafka    │ (Port: 9092)
        │  (Message   │
        │   Broker)   │
        └─────────────┘
               │
        ┌──────▼──────┐
        │  Zookeeper  │ (Port: 2181)
        └─────────────┘
```

---

## Services

### 1. Eureka Server (Service Discovery)
- **Port**: 8761
- **Purpose**: Service registry for microservices discovery
- **Technology**: Spring Cloud Netflix Eureka

### 2. API Gateway
- **Port**: 9000
- **Purpose**: Single entry point for all client requests
- **Features**:
  - Request routing
  - Load balancing
  - Circuit breaker (Resilience4j)
  - Distributed tracing (Micrometer + Zipkin)
- **Technology**: Spring Cloud Gateway

### 3. User Service (Revision)
- **Port**: 8080
- **Database**: MySQL (Port: 3307)
- **Purpose**: Manages user data and publishes user events
- **Key Features**:
  - User CRUD operations
  - Kafka event producer
  - Distributed tracing
  - Circuit breaker support

**Endpoints**:
- `POST /users/addUser` - Create new user
- `GET /users/{id}` - Get user by ID
- `POST /users/publish` - Publish user event to Kafka

### 4. Order Service
- **Port**: 8081
- **Database**: MySQL (Port: 3307)
- **Purpose**: Handles order creation and consumes user events
- **Key Features**:
  - Asynchronous order processing
  - Kafka consumer (user events)
  - Kafka producer (order events)
  - OpenFeign client (commented)
  - WebClient support
  - Distributed tracing

**Endpoints**:
- `GET /orders/create/{id}` - Create order asynchronously

### 5. Payment Service
- **Port**: 8082
- **Purpose**: Processes payments by consuming order events
- **Key Features**:
  - Kafka consumer (order events)
  - Distributed tracing

---

## Request Flow Diagrams

### Flow 1: Create User and Publish Event

```
┌────────┐     ┌─────────────┐     ┌──────────┐     ┌───────────┐     ┌─────────────┐
│ Client │────▶│ API Gateway │────▶│  Eureka  │────▶│   User    │────▶│   MySQL     │
└────────┘     └─────────────┘     │  Server  │     │  Service  │     │  Database   │
                                    └──────────┘     │ (8080)    │     └─────────────┘
                                                     └─────┬─────┘
                                                           │
                                                           ▼
                                                     ┌──────────┐
                                                     │  Kafka   │
                                                     │  Topic:  │
                                                     │  order-  │
                                                     │  topic   │
                                                     └──────────┘

Request: POST /users/addUser
Body: {
  "name": "John Doe",
  "email": "john@example.com"
}

Steps:
1. Client sends POST request to API Gateway (9000)
2. API Gateway queries Eureka Server for User Service location
3. API Gateway routes request to User Service (8080)
4. User Service saves user to MySQL database
5. User Service returns UserDto to client

Response: {
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

### Flow 2: Publish User Event (Event-Driven)

```
┌────────┐     ┌─────────────┐     ┌───────────┐     ┌──────────┐
│ Client │────▶│ API Gateway │────▶│   User    │────▶│  Kafka   │
└────────┘     └─────────────┘     │  Service  │     │  Broker  │
                                    │ (8080)    │     └────┬─────┘
                                    └───────────┘          │
                                                           │
                                    ┌──────────────────────┴──────────────────┐
                                    │                                         │
                                    ▼                                         ▼
                            ┌───────────────┐                        ┌──────────────┐
                            │ Order Service │                        │   Payment    │
                            │   Consumer    │                        │   Service    │
                            │   (8081)      │                        │  Consumer    │
                            └───────────────┘                        │   (8082)     │
                                                                     └──────────────┘

Request: POST /users/publish
Body: {
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}

Steps:
1. Client sends POST request to API Gateway
2. API Gateway routes to User Service
3. User Service publishes UserEvent to Kafka topic "order-topic"
4. Kafka stores message in topic
5. Order Service consumes message (group: order-group)
6. Payment Service consumes message (group: payment-group)
7. User Service returns confirmation immediately (async)

Response: "Message sent for user: John Doe"

Kafka Message Flow:
- Topic: order-topic
- Message: UserEvent {id=1, name="John Doe", email="john@example.com"}
- Consumers: 
  * Order Service (order-group)
  * Payment Service (payment-group)
```

### Flow 3: Create Order (Asynchronous Processing)

```
┌────────┐     ┌─────────────┐     ┌───────────────┐     ┌──────────────┐
│ Client │────▶│ API Gateway │────▶│ Order Service │────▶│ AsyncService │
└────────┘     └─────────────┘     │    (8081)     │     │   (@Async)   │
                                    └───────────────┘     └──────────────┘
                                            │
                                            │ (immediate response)
                                            ▼
                                    ┌───────────────┐
                                    │    Client     │
                                    │   receives    │
                                    │   response    │
                                    └───────────────┘

Request: GET /orders/create/12345

Steps:
1. Client sends GET request to API Gateway
2. API Gateway routes to Order Service (8081)
3. Order Service calls AsyncService.processOrder(12345)
4. AsyncService starts processing in separate thread (@Async)
5. Order Service immediately returns response to client
6. AsyncService continues processing in background (3 second delay)

Response (immediate): "order received, process started asynchronously 12345"

Background Processing:
- Thread starts: "started async: 12345"
- Waits 3 seconds
- Thread completes: "completed async: 12345"
```

### Flow 4: Complete User-to-Payment Flow

```
┌────────┐
│ Client │
└───┬────┘
    │ 1. POST /users/addUser
    ▼
┌─────────────┐
│ API Gateway │
│   (9000)    │
└──────┬──────┘
       │ 2. Route via Eureka
       ▼
┌──────────────┐
│ User Service │
│   (8080)     │
└──────┬───────┘
       │ 3. Save to DB
       ▼
┌──────────────┐
│    MySQL     │
└──────────────┘
       │ 4. POST /users/publish
       ▼
┌──────────────┐
│ User Service │
│  (Producer)  │
└──────┬───────┘
       │ 5. Publish UserEvent
       ▼
┌──────────────┐
│    Kafka     │
│ order-topic  │
└──────┬───────┘
       │
       ├─────────────────────┐
       │                     │
       ▼                     ▼
┌──────────────┐    ┌──────────────┐
│Order Service │    │   Payment    │
│  (Consumer)  │    │   Service    │
│   (8081)     │    │  (Consumer)  │
└──────────────┘    │   (8082)     │
                    └──────────────┘
                           │
                           ▼
                    ┌──────────────┐
                    │  Process     │
                    │  Payment     │
                    └──────────────┘

Complete Flow:
1. Create user via User Service
2. User saved to MySQL database
3. Publish user event to Kafka
4. Order Service receives event and processes
5. Payment Service receives event and processes payment
6. All services trace requests via Zipkin
```

### Flow 5: Service Discovery Flow

```
┌─────────────────┐
│  Microservice   │
│   Startup       │
└────────┬────────┘
         │ 1. Register
         ▼
┌─────────────────┐
│ Eureka Server   │
│    (8761)       │
└────────┬────────┘
         │ 2. Heartbeat (every 30s)
         │
         │ 3. Service lookup request
         ▼
┌─────────────────┐
│  API Gateway    │
│  or Consumer    │
└─────────────────┘

Services Registered:
- revision (User Service) - 8080
- order-microservice - 8081
- payment - 8082
- api-gateway - 9000
```

---

## Technology Stack

### Core Framework
- **Spring Boot**: 3.5.7
- **Java**: 21
- **Spring Cloud**: 2025.0.0

### Service Discovery & Gateway
- **Eureka Server**: Service registry
- **Spring Cloud Gateway**: API Gateway with WebFlux

### Messaging
- **Apache Kafka**: Event streaming (Confluent 7.5.0)
- **Zookeeper**: Kafka coordination

### Database
- **MySQL**: 8.x
- **Spring Data JPA**: ORM
- **Hibernate**: JPA implementation

### Communication
- **OpenFeign**: Declarative REST client
- **WebClient**: Reactive HTTP client
- **RestTemplate**: Synchronous HTTP client

### Resilience
- **Resilience4j**: Circuit breaker, retry, rate limiter
- **Spring AOP**: Aspect-oriented programming

### Observability
- **Micrometer Tracing**: Distributed tracing
- **OpenTelemetry**: Tracing bridge
- **Zipkin**: Trace visualization
- **Spring Actuator**: Metrics and health checks

### Containerization
- **Docker**: Container runtime
- **Docker Compose**: Multi-container orchestration

---

## Setup and Deployment

### Prerequisites
- Java 21
- Maven 3.x
- Docker & Docker Compose

### Local Development

1. **Start Infrastructure**:
```bash
docker-compose up -d zookeeper kafka mysql eureka-server
```

2. **Build Services**:
```bash
mvn clean install
```

3. **Run Services**:
```bash
# Terminal 1 - User Service
cd revision
mvn spring-boot:run

# Terminal 2 - Order Service
cd order-microservice
mvn spring-boot:run

# Terminal 3 - Payment Service
cd payment
mvn spring-boot:run

# Terminal 4 - API Gateway
cd api-gateway
mvn spring-boot:run
```

### Docker Deployment

```bash
docker-compose up -d
```

This starts:
- Zookeeper (2181)
- Kafka (9092, 9093)
- MySQL (3307)
- Eureka Server (8761)
- API Gateway (9000)
- User Service (8080)
- Order Service (8081)
- Payment Service (8082)

### Service URLs

| Service | URL | Health Check |
|---------|-----|--------------|
| Eureka Dashboard | http://localhost:8761 | http://localhost:8761/actuator/health |
| API Gateway | http://localhost:9000 | http://localhost:9000/actuator/health |
| User Service | http://localhost:8080 | http://localhost:8080/actuator/health |
| Order Service | http://localhost:8081 | http://localhost:8081/actuator/health |
| Payment Service | http://localhost:8082 | http://localhost:8082/actuator/health |

---

## Configuration

### Profiles
- **localhost**: Local development
- **docker**: Docker container deployment

### Kafka Configuration

**Topics**:
- `order-topic`: User events and order events

**Consumer Groups**:
- `order-group`: Order Service
- `payment-group`: Payment Service

### Database Configuration
- **Host**: localhost (local) / mysql-db (docker)
- **Port**: 3307 (external) / 3306 (internal)
- **Database**: msrevision
- **Credentials**: Configured in application properties

---

## Key Features

### 1. Event-Driven Architecture
- Asynchronous communication via Kafka
- Decoupled services
- Multiple consumers per topic

### 2. Service Discovery
- Dynamic service registration
- Client-side load balancing
- Health monitoring

### 3. API Gateway Pattern
- Single entry point
- Request routing
- Circuit breaker integration

### 4. Distributed Tracing
- Request correlation across services
- Trace ID propagation
- Zipkin integration

### 5. Resilience Patterns
- Circuit breaker
- Retry mechanism
- Fallback strategies

### 6. Asynchronous Processing
- @Async annotation
- Non-blocking operations
- CompletableFuture support

---

## API Examples

### Create User
```bash
curl -X POST http://localhost:9000/users/addUser \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com"
  }'
```

### Get User
```bash
curl http://localhost:9000/users/1
```

### Publish User Event
```bash
curl -X POST http://localhost:9000/users/publish \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }'
```

### Create Order
```bash
curl http://localhost:9000/orders/create/12345
```

---

## Monitoring

### Eureka Dashboard
View registered services: http://localhost:8761

### Actuator Endpoints
- `/actuator/health` - Health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application info

### Zipkin Tracing
Configure Zipkin URL in application properties to view distributed traces.

---

## Project Structure

```
micoservice-app/
├── api-gateway/          # API Gateway service
├── erurekaserver/        # Service discovery
├── revision/             # User service
├── order-microservice/   # Order service
├── payment/              # Payment service
├── docker-compose.yml    # Container orchestration
└── pom.xml              # Parent POM
```

---

## Future Enhancements

1. **Security**: Add Spring Security with OAuth2/JWT
2. **Config Server**: Centralized configuration management
3. **API Documentation**: Swagger/OpenAPI integration
4. **Monitoring**: Prometheus + Grafana dashboards
5. **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
6. **Testing**: Integration and contract testing
7. **CI/CD**: Jenkins/GitHub Actions pipeline

---

## Troubleshooting

### Kafka Connection Issues
- Ensure Zookeeper is running before Kafka
- Check `KAFKA_ADVERTISED_LISTENERS` configuration
- Verify network connectivity between services

### Service Registration Issues
- Verify Eureka Server is running
- Check `eureka.client.serviceUrl.defaultZone` configuration
- Review service logs for registration errors

### Database Connection Issues
- Ensure MySQL container is healthy
- Verify database credentials
- Check network connectivity

---

## License
This project is for educational purposes.

## Contributors
Microservices Team
