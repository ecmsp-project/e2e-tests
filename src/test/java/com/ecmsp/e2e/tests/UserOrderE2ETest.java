package com.ecmsp.e2e.tests;

import com.ecmsp.e2e.client.AuthClient;
import com.ecmsp.e2e.client.OrderClient;
import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.order.GetOrderResponseDto;
import com.ecmsp.e2e.dto.login.LoginResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Order E2E Tests")
public class UserOrderE2ETest {

    private static AuthClient authClient;
    private static OrderClient orderClient;
    private static String jwtToken;

    @BeforeAll
    public static void setUp() {
        authClient = new AuthClient();
        orderClient = new OrderClient();

        System.out.println("Starting E2E tests...");
        System.out.println("Gateway URL: " + TestConfig.getGatewayUrl());
        System.out.println("Test User: " + TestConfig.getTestUsername());
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("1. User should be able to login successfully")
    public void user_should_login_successfully() {
        String username = TestConfig.getTestUsername();
        String password = TestConfig.getTestPassword();

        LoginResponse loginResponse = authClient.login(username, password);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.token()).isNotNull().isNotEmpty();

        jwtToken = loginResponse.token();

        System.out.println("✓ Login successful");
        System.out.println("Token: " + jwtToken.substring(0, Math.min(50, jwtToken.length())) + "...");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("2. User should receive JWT token with proper format")
    public void login_should_return_valid_jwt_token() {
        String username = TestConfig.getTestUsername();
        String password = TestConfig.getTestPassword();

        Response response = authClient.loginRaw(username, password);

        response.then().statusCode(200);

        LoginResponse loginResponse = response.as(LoginResponse.class);
        String token = loginResponse.token();

        assertThat(token.split("\\.")).hasSize(3);

        System.out.println("✓ JWT token format validated");
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("3. User should be able to fetch their orders using token")
    public void user_should_fetch_orders_with_token() {
        if (jwtToken == null) {
            jwtToken = authClient.getJwtToken(
                    TestConfig.getTestUsername(),
                    TestConfig.getTestPassword()
            );
        }

        Response response = orderClient.getOrdersViaRestRaw(jwtToken);

        response.then().statusCode(200);

        List<GetOrderResponseDto> orders = orderClient.getOrdersViaRest(jwtToken);
        assertThat(orders).isNotNull();

        System.out.println("✓ Successfully fetched orders");
        System.out.println("Number of orders: " + orders.size());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("4. Authenticated request should fail without token")
    public void order_request_should_fail_without_token() {
        Response response = orderClient.getOrdersViaRestRaw("");

        assertThat(response.getStatusCode()).isIn(401, 403);

        System.out.println("✓ Unauthorized access properly blocked");
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("5. Login should fail with invalid credentials")
    public void login_should_fail_with_invalid_credentials() {
        Response response = authClient.loginRaw("invaliduser", "wrongpassword");

        assertThat(response.getStatusCode()).isIn(401, 403, 500);

        System.out.println("✓ Invalid credentials properly rejected");
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("6. Complete flow: Login and fetch orders")
    public void complete_login_and_fetch_orders_flow_should_work() {
        String username = TestConfig.getTestUsername();
        String password = TestConfig.getTestPassword();

        LoginResponse loginResponse = authClient.login(username, password);
        String token = loginResponse.token();

        assertThat(token).isNotNull().isNotEmpty();
        System.out.println("✓ Step 1: Login successful");

        List<GetOrderResponseDto> orders = orderClient.getOrdersViaRest(token);

        assertThat(orders).isNotNull();
        System.out.println("✓ Step 2: Orders fetched successfully");

        if (!orders.isEmpty()) {
            GetOrderResponseDto firstOrder = orders.get(0);
            assertThat(firstOrder.orderId()).isNotNull();
            assertThat(firstOrder.orderStatus()).isNotNull();
            assertThat(firstOrder.date()).isNotNull();
            assertThat(firstOrder.items()).isNotNull();

            System.out.println("✓ Step 3: Order data structure validated");
            System.out.println("First Order ID: " + firstOrder.orderId());
            System.out.println("Order Status: " + firstOrder.orderStatus());
        }

        System.out.println("✓ Complete E2E flow successful");
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    @DisplayName("7. Complete flow: Login and fetch orders via gRPC")
    public void complete_login_and_fetch_orders_via_grpc_flow_should_work() {
        String username = TestConfig.getTestUsername();
        String password = TestConfig.getTestPassword();

        LoginResponse loginResponse = authClient.login(username, password);
        String token = loginResponse.token();

        assertThat(token).isNotNull().isNotEmpty();
        System.out.println("✓ Step 1: Login successful");

        Response response = orderClient.getOrdersRaw(token);
        response.then().statusCode(200);

        List<GetOrderResponseDto> orders = orderClient.getOrders(token);

        assertThat(orders).isNotNull();
        System.out.println("✓ Step 2: Orders fetched via gRPC successfully");

        if (!orders.isEmpty()) {
            GetOrderResponseDto firstOrder = orders.get(0);
            assertThat(firstOrder.orderId()).isNotNull();
            assertThat(firstOrder.orderStatus()).isNotNull();
            assertThat(firstOrder.date()).isNotNull();
            assertThat(firstOrder.items()).isNotNull();

            System.out.println("✓ Step 3: Order data structure validated");
            System.out.println("First Order ID: " + firstOrder.orderId());
            System.out.println("Order Status: " + firstOrder.orderStatus());
        }

        System.out.println("✓ Complete gRPC E2E flow successful");
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("\nE2E tests completed!");
    }
}