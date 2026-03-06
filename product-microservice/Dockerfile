FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/product-microservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

# Run as non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
