# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an end-to-end (E2E) test suite for a microservices-based e-commerce system. The project tests the integration between multiple services (Gateway, Order Service, User Service) using REST API calls. Tests are written in Java 21 using JUnit 5, Rest Assured, and AssertJ.

## Architecture

The project follows a client-based architecture pattern for E2E testing:

- **Client Layer** (`src/main/java/com/ecmsp/e2e/client/`): Contains HTTP client wrappers for each service
  - `AuthClient`: Handles authentication/login operations via gateway
  - `OrderClient`: Handles order-related operations via gateway
  - All clients use Rest Assured and interact with services through the API Gateway

- **Configuration** (`src/main/java/com/ecmsp/e2e/config/`): Centralized configuration management
  - `TestConfig`: Loads and provides access to service URLs, credentials, and test settings from `test.properties`

- **DTOs** (`src/main/java/com/ecmsp/e2e/dto/`): Data transfer objects organized by domain
  - `login/`: Login request/response DTOs
  - `order/`: Order-related DTOs (Order, OrderItem, OrderStatus)

- **Tests** (`src/test/java/com/ecmsp/e2e/tests/`): E2E test scenarios
  - `UserOrderE2ETest`: Main test class covering authentication and order retrieval flows
  - Tests are ordered using `@Order` annotation to ensure proper execution sequence
  - Tests validate complete flows: login → token validation → authenticated requests

## Common Commands

### Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=UserOrderE2ETest

# Run a specific test method
mvn test -Dtest=UserOrderE2ETest#user_should_login_successfully

# Run tests with verbose output
mvn test -X
```

### Building

```bash
# Clean and compile
mvn clean compile

# Clean and package
mvn clean package

# Skip tests during build
mvn clean package -DskipTests
```

## Test Configuration

Service endpoints and test credentials are configured in `src/test/resources/test.properties`:
- Gateway URL: Default `http://localhost:8600`
- Order Service URL: Default `http://localhost:8300`
- User Service URL: Default `http://localhost:8500`
- Test user credentials: `andy` / `password123`

**Important**: Before running tests, ensure all microservices (Gateway, Order Service, User Service) are running on their configured ports.

## Key Testing Patterns

1. **Shared JWT Token**: Tests use a static `jwtToken` field to share authentication state across test methods
2. **Ordered Test Execution**: Tests run sequentially using `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`
3. **Dual Response Methods**: Clients provide both typed (e.g., `getMyOrders()`) and raw response (e.g., `getMyOrdersRaw()`) methods for flexibility
4. **Token Authentication**: All protected endpoints require `Authorization: Bearer <token>` header
5. **Negative Testing**: Tests include scenarios for unauthorized access and invalid credentials

## Dependencies

- JUnit Jupiter 5.10.0 (test framework)
- Rest Assured 5.3.2 (HTTP client for testing)
- Jackson 2.15.2 (JSON serialization)
- AssertJ 3.24.2 (fluent assertions)