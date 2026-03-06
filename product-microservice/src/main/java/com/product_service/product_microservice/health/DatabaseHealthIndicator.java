package com.product_service.product_microservice.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                        .withDetail("database", "MySQL")
                        .withDetail("status", "Connected")
                        .build();
            }
        } catch (java.sql.SQLException e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
        return Health.down().build();
    }
}
