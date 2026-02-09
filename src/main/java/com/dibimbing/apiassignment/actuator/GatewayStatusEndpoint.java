package com.dibimbing.apiassignment.actuator;


import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom Gateway Status Endpoint for monitoring inventory system health.
 * This endpoint provides detailed information about the system's operational status,
 * including database connectivity, cache status, and inventory metrics.
 *
 * Endpoint: GET /actuator/gateway-status
 */
@Component
@Endpoint(id = "gateway-status")
public class GatewayStatusEndpoint {

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Get the gateway status with detailed health information.
     *
     * @return Map containing gateway status information
     */

    @ReadOperation
    public Map<String, Object> gatewayStatus() {
        Map<String, Object> statusMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        statusMap.put("timestamp", LocalDateTime.now().format(formatter));
        statusMap.put("service_name", "simple-inventory");
        statusMap.put("status", "UP");
        statusMap.put("database", checkDatabaseStatus());
        statusMap.put("cache", checkCacheStatus());
        statusMap.put("inventory", checkInventoryStatus());
        statusMap.put("summary", generateStatusSummary(statusMap));

        return statusMap;
    }

    /**
     * Check database connectivity and retrieve basic statistics.
     */
    private Map<String, Object> checkDatabaseStatus() {
        Map<String, Object> dbStatus = new HashMap<>();

        try {
            Connection connection = dataSource.getConnection();
            if (connection != null && !connection.isClosed()) {
                dbStatus.put("status", "CONNECTED");
                dbStatus.put("type", "MySQL");
                dbStatus.put("available", true);
                connection.close();
            } else {
                dbStatus.put("status", "DISCONNECTED");
                dbStatus.put("available", false);
            }
        } catch (Exception e) {
            dbStatus.put("status", "ERROR");
            dbStatus.put("error", e.getMessage());
            dbStatus.put("available", false);
        }

        return dbStatus;
    }

    /**
     * Check Redis cache connectivity.
     */
    private Map<String, Object> checkCacheStatus() {
        Map<String, Object> cacheStatus = new HashMap<>();

        try {
            if (redisTemplate != null) {
                redisTemplate.getConnectionFactory().getConnection().ping();
                cacheStatus.put("status", "CONNECTED");
                cacheStatus.put("type", "Redis");
                cacheStatus.put("available", true);
            } else {
                cacheStatus.put("status", "NOT_CONFIGURED");
                cacheStatus.put("available", false);
            }
        } catch (Exception e) {
            cacheStatus.put("status", "ERROR");
            cacheStatus.put("error", e.getMessage());
            cacheStatus.put("available", false);
        }

        return cacheStatus;
    }

    /**
     * Check inventory status based on product stock levels.
     * This is a basic implementation that should be enhanced based on your domain.
     */
    private Map<String, Object> checkInventoryStatus() {
        Map<String, Object> inventoryStatus = new HashMap<>();

        try {
            // This is a placeholder - implement actual product stock checking logic
            // You would inject a repository or service to check product counts

            inventoryStatus.put("status", "OPERATIONAL");
            inventoryStatus.put("total_products", 0);  // Replace with actual count
            inventoryStatus.put("low_stock_items", 0);  // Replace with actual count
            inventoryStatus.put("out_of_stock_items", 0);  // Replace with actual count
            inventoryStatus.put("health_percentage", 100);

        } catch (Exception e) {
            inventoryStatus.put("status", "ERROR");
            inventoryStatus.put("error", e.getMessage());
        }

        return inventoryStatus;
    }

    /**
     * Generate a summary of the overall system status.
     */
    private Map<String, Object> generateStatusSummary(Map<String, Object> statusMap) {
        Map<String, Object> summary = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<String, Object> dbStatus = (Map<String, Object>) statusMap.get("database");
        @SuppressWarnings("unchecked")
        Map<String, Object> cacheStatus = (Map<String, Object>) statusMap.get("cache");
        @SuppressWarnings("unchecked")
        Map<String, Object> inventoryStatus = (Map<String, Object>) statusMap.get("inventory");

        boolean dbHealthy = (boolean) dbStatus.getOrDefault("available", false);
        boolean cacheHealthy = (boolean) cacheStatus.getOrDefault("available", false);
        boolean inventoryHealthy = "OPERATIONAL".equals(inventoryStatus.get("status"));

        int healthScore = 0;
        if (dbHealthy) healthScore += 40;
        if (cacheHealthy) healthScore += 30;
        if (inventoryHealthy) healthScore += 30;

        summary.put("health_score", healthScore);
        summary.put("all_systems_operational", dbHealthy && cacheHealthy && inventoryHealthy);
        summary.put("components_healthy",
                (dbHealthy ? 1 : 0) + (cacheHealthy ? 1 : 0) + (inventoryHealthy ? 1 : 0) + "/3");

        return summary;
    }
}
