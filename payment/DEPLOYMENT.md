# Payment Microservice - Deployment Guide

## Quick Start

### Docker Deployment
```bash
mvn clean package -DskipTests
docker build -t payment-microservice:latest .
docker-compose up -d payment
```

### Verify Deployment
```bash
curl http://localhost:8082/actuator/health
```

## Environment Configuration

Create `.env` file:
```bash
KAFKA_BOOTSTRAP_SERVERS=kafka:9093
ALLOWED_ORIGINS=http://localhost:3000
SPRING_PROFILES_ACTIVE=production
```

## Monitoring

### Health Check
```bash
curl http://localhost:8082/actuator/health
```

### Metrics
```bash
curl http://localhost:8082/actuator/metrics
```

### Logs
```bash
# Docker logs
docker logs payment-service

# File logs
tail -f logs/payment-microservice.log
```

## Troubleshooting

### Kafka Connection Issues
- Verify Kafka is running
- Check KAFKA_BOOTSTRAP_SERVERS configuration
- Review logs for connection errors

### Message Processing Failures
- Check logs for error details
- Verify message format
- Ensure Kafka topic exists

## Production Checklist

- [ ] Kafka cluster is running
- [ ] Environment variables configured
- [ ] Health check returns 200 OK
- [ ] Logs are being written
- [ ] Metrics are accessible
- [ ] CORS origins configured
