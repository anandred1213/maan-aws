# Production Deployment Guide

## Pre-Deployment Checklist

### 1. Security
- [ ] Change all default passwords
- [ ] Configure secure database credentials
- [ ] Set up HTTPS/TLS certificates
- [ ] Configure firewall rules
- [ ] Enable security headers
- [ ] Review CORS allowed origins
- [ ] Disable Swagger UI in production (optional)

### 2. Database
- [ ] Create production database
- [ ] Set up database backups
- [ ] Configure connection pooling
- [ ] Run database migrations
- [ ] Set up monitoring

### 3. Application Configuration
- [ ] Set SPRING_PROFILES_ACTIVE=production
- [ ] Configure environment variables
- [ ] Set up logging directories
- [ ] Configure log rotation
- [ ] Set appropriate JVM memory settings

### 4. Infrastructure
- [ ] Set up load balancer
- [ ] Configure auto-scaling
- [ ] Set up monitoring (Prometheus/Grafana)
- [ ] Configure alerting
- [ ] Set up centralized logging (ELK/CloudWatch)

## Deployment Steps

### Option 1: Docker Deployment

1. **Build the application:**
```bash
mvn clean package -DskipTests
```

2. **Create environment file:**
```bash
cp .env.example .env
# Edit .env with production values
```

3. **Build Docker image:**
```bash
docker build -t product-microservice:latest .
```

4. **Run with Docker Compose:**
```bash
docker-compose up -d product-microservice
```

5. **Verify deployment:**
```bash
curl http://localhost:8083/actuator/health
```

### Option 2: Kubernetes Deployment

1. **Create ConfigMap:**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: product-config
data:
  SPRING_PROFILES_ACTIVE: "production"
  EUREKA_URL: "http://eureka-server:8761/eureka/"
```

2. **Create Secret:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: product-secrets
type: Opaque
stringData:
  DB_PASSWORD: "your-secure-password"
```

3. **Deploy application:**
```bash
kubectl apply -f k8s/
```

### Option 3: Traditional Server Deployment

1. **Build JAR:**
```bash
mvn clean package -DskipTests
```

2. **Copy to server:**
```bash
scp target/product-microservice-0.0.1-SNAPSHOT.jar user@server:/opt/app/
```

3. **Create systemd service:**
```ini
[Unit]
Description=Product Microservice
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/app
ExecStart=/usr/bin/java -jar -Xmx512m -Xms256m \
  -Dspring.profiles.active=production \
  /opt/app/product-microservice-0.0.1-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

4. **Start service:**
```bash
sudo systemctl enable product-microservice
sudo systemctl start product-microservice
```

## Monitoring

### Health Check Endpoints
- Application: `http://localhost:8083/actuator/health`
- Database: Check `database` component in health response
- Eureka: Check service registration

### Metrics
- Prometheus: `http://localhost:8083/actuator/prometheus`
- Custom metrics: `http://localhost:8083/actuator/metrics`

### Logging
- Application logs: `/var/log/product-microservice.log`
- Access logs: Configure in application properties
- Error tracking: Integrate Sentry/Rollbar

## Performance Tuning

### JVM Options
```bash
-XX:+UseContainerSupport
-XX:MaxRAMPercentage=75.0
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/heapdump.hprof
```

### Database Connection Pool
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
```

## Rollback Procedure

1. **Stop current version:**
```bash
docker-compose stop product-microservice
```

2. **Deploy previous version:**
```bash
docker tag product-microservice:previous product-microservice:latest
docker-compose up -d product-microservice
```

3. **Verify rollback:**
```bash
curl http://localhost:8083/actuator/health
```

## Troubleshooting

### Application won't start
- Check logs: `docker logs product-service`
- Verify database connectivity
- Check environment variables
- Verify Eureka server is running

### High memory usage
- Review JVM settings
- Check for memory leaks
- Analyze heap dump
- Adjust connection pool size

### Slow response times
- Check database query performance
- Review connection pool settings
- Enable query logging
- Check network latency

## Backup and Recovery

### Database Backup
```bash
docker exec mysql-db mysqldump -u root -p product_db > backup.sql
```

### Restore Database
```bash
docker exec -i mysql-db mysql -u root -p product_db < backup.sql
```

## Security Best Practices

1. **Never expose actuator endpoints publicly**
2. **Use secrets management (Vault, AWS Secrets Manager)**
3. **Enable HTTPS only**
4. **Implement rate limiting**
5. **Regular security updates**
6. **Use least privilege principle**
7. **Enable audit logging**
8. **Implement API authentication/authorization**

## Post-Deployment Verification

- [ ] Health check returns 200 OK
- [ ] All endpoints responding correctly
- [ ] Database connectivity verified
- [ ] Eureka registration successful
- [ ] Logs are being written
- [ ] Metrics are being collected
- [ ] Alerts are configured
- [ ] Backup jobs running
