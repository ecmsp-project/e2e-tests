package com.ecmsp.e2e.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader()
                .getResourceAsStream("test.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find test.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load test.properties", ex);
        }
    }

    public static String getGatewayUrl() {
        return properties.getProperty("services.gateway-service.url");
    }

    public static String getOrderServiceUrl() {
        return properties.getProperty("services.order-service.url");
    }

    public static String getUserServiceUrl() {
        return properties.getProperty("services.user-service.url");
    }

    public static String getTestUsername() {
        return properties.getProperty("test.user.login");
    }

    public static String getTestPassword() {
        return properties.getProperty("test.user.password");
    }
}