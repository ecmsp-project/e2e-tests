# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an end-to-end (E2E) test suite for a microservices-based e-commerce system. The project tests the integration between multiple services (Gateway, Order Service, User Service, Return Service) using REST API calls and gRPC. Tests are written in Java 21 using JUnit 5, Rest Assured, and AssertJ.

## Architecture

The project follows a client-based architecture pattern for E2E testing:

- **Client Layer** (`src/main/java/com/ecmsp/e2e/client/`): Contains HTTP client wrappers for each service
  - `AuthClient`: Handles authentication/login operations via gateway
  - `OrderClient`: Handles order-related operations via gateway (both REST and gRPC endpoints)
  - `ReturnClient`: Handles return/refund operations via gateway
  - All clients use Rest Assured and interact with services through the API Gateway
  - Each client provides both typed methods (e.g., `getOrders()`) and raw response methods (e.g., `getOrdersRaw()`) for flexibility in testing

- **Configuration** (`src/main/java/com/ecmsp/e2e/config/`): Centralized configuration management
  - `TestConfig`: Loads and provides access to service URLs, credentials, and test settings from `test.properties`

- **DTOs** (`src/main/java/com/ecmsp/e2e/dto/`): Data transfer objects organized by domain
  - `login/`: Login request/response DTOs
  - `order/`: Order-related DTOs (Order, OrderItem, OrderStatus)
  - `returns/`: Return-related DTOs (ReturnToCreate, ReturnOrder, CreateReturnResponseDto, etc.)

- **Tests** (`src/test/java/com/ecmsp/e2e/tests/`): E2E test scenarios
  - `UserOrderE2ETest`: Authentication and order retrieval flows with sequential test execution
  - `OrderE2ETest`: Comprehensive order operations (get orders, get by ID, get items, get status)
  - `ReturnE2ETest`: Complete return/refund flows including creation, retrieval, and authentication
  - Tests validate complete flows: login → token validation → authenticated requests
  - Many tests include negative scenarios (unauthorized access, invalid credentials)

## Common Commands

### Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=UserOrderE2ETest
mvn test -Dtest=OrderE2ETest
mvn test -Dtest=ReturnE2ETest

# Run a specific test method
mvn test -Dtest=UserOrderE2ETest#user_should_login_successfully
mvn test -Dtest=ReturnE2ETest#should_create_return_for_existing_order

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
- Request timeout: 5000ms

**Important**: Before running tests, ensure all microservices (Gateway, Order Service, User Service, Return Service) are running on their configured ports.

## Key Testing Patterns

1. **Dual Response Methods**: All clients provide both typed methods (e.g., `getOrders()`) that return DTOs and raw methods (e.g., `getOrdersRaw()`) that return `Response` objects. Use typed methods for happy path testing with automatic deserialization, and raw methods for negative testing or when you need to inspect status codes/headers.

2. **JWT Token Authentication**:
   - All protected endpoints require `Authorization: Bearer <token>` header
   - Most tests obtain tokens using `authClient.getJwtToken(username, password)`
   - `UserOrderE2ETest` uses a static `jwtToken` field shared across sequential test methods
   - Other test classes (`OrderE2ETest`, `ReturnE2ETest`) authenticate per-test using `@BeforeEach` or directly in test methods

3. **Test Execution Modes**:
   - `UserOrderE2ETest`: Sequential execution using `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` and `@Order` annotations
   - `OrderE2ETest` and `ReturnE2ETest`: Independent test methods that can run in any order

4. **REST vs gRPC Endpoints**: `OrderClient` supports both:
   - REST endpoints: `getOrdersViaRest()` → `/api/orders`
   - gRPC endpoints: `getOrders()` → `/api/orders/grpc`
   - By default, tests use gRPC endpoints

5. **Negative Testing**: Tests include scenarios for:
   - Unauthorized access (401/403 status codes)
   - Invalid credentials
   - Missing authentication tokens
   - Expected behavior: `response.getStatusCode()).isIn(401, 403)`

6. **Complete Flow Testing**: Many tests validate end-to-end flows spanning authentication, data creation, retrieval, and verification (see `ReturnE2ETest.complete_flow_login_create_return_and_verify()`)

## Dependencies

- JUnit Jupiter 5.10.0 (test framework)
- Rest Assured 5.3.2 (HTTP client for testing)
- Jackson 2.15.2 (JSON serialization with JSR-310 support for Java 8 date/time)
- AssertJ 3.24.2 (fluent assertions)
- Maven Surefire Plugin 3.1.2 (test execution)