# Product Microservice

## Overview
Production-ready product microservice for e-commerce platform handling product management operations.

## Prerequisites
- Java 21
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose (for containerized deployment)

## Features
- ✅ Input validation with Bean Validation
- ✅ Global exception handling
- ✅ Health checks and monitoring (Spring Actuator)
- ✅ Connection pooling (HikariCP)
- ✅ Structured logging with Logback
- ✅ Secure CORS configuration
- ✅ Docker support with health checks
- ✅ Production and development profiles
- ✅ API documentation (Swagger/OpenAPI)

## Setup

### Local Development

1. Create database:
```sql
CREATE DATABASE product_db;
```

2. Update credentials in `application-localhost.properties` if needed

3. Build:
```bash
mvn clean install
```

4. Run:
```bash
mvn spring-boot:run
```

### Production Deployment

1. Create `.env` file from template:
```bash
cp .env.example .env
```

2. Update `.env` with secure credentials

3. Build with Maven:
```bash
mvn clean package -DskipTests
```

4. Run with Docker Compose:
```bash
docker-compose up -d product-microservice
```

## API Endpoints

- `POST /products/add` - Add product (requires validation)
- `GET /products` - Get all products
- `GET /products/{id}` - Get product by ID
- `PUT /products/update/{id}` - Update product
- `DELETE /products/delete/{id}` - Delete product

## Monitoring Endpoints

- `GET /actuator/health` - Health check
- `GET /actuator/info` - Application info
- `GET /actuator/metrics` - Metrics
- `GET /swagger-ui.html` - API Documentation

## Configuration Profiles

- `localhost` - Local development
- `docker` - Docker environment
- `production` - Production deployment

## Environment Variables

| Variable | Description | Default |
|----------|-------------|----------|
| DB_URL | Database URL | jdbc:mysql://mysql:3306/product_db |
| DB_USERNAME | Database username | root |
| DB_PASSWORD | Database password | (required) |
| EUREKA_URL | Eureka server URL | http://eureka-server:8761/eureka/ |
| ALLOWED_ORIGINS | CORS allowed origins | http://localhost:3000 |
| SPRING_PROFILES_ACTIVE | Active profile | production |

## Port
8083

## Security Considerations

- Never commit `.env` file with real credentials
- Use strong passwords for production databases
- Configure CORS with specific allowed origins
- Run Docker containers as non-root user
- Enable HTTPS in production
- Regularly update dependencies

## Logging

Logs are stored in:
- Console output (development)
- `/var/log/product-microservice.log` (production)
- Rolling file policy: 10MB per file, 30 days retention
