package com.ecmsp.e2e.tests;

import com.ecmsp.e2e.client.AuthClient;
import com.ecmsp.e2e.client.OrderClient;
import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.order.Order;
import com.ecmsp.e2e.dto.order.OrderItem;
import com.ecmsp.e2e.dto.order.OrderStatusResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Order E2E Tests")
public class OrderE2ETest {

    private static AuthClient authClient;
    private static OrderClient orderClient;

    @BeforeAll
    public static void setUp() {
        authClient = new AuthClient();
        orderClient = new OrderClient();

        System.out.println("Starting Order E2E tests...");
        System.out.println("Gateway URL: " + TestConfig.getGatewayUrl());
    }

    @Test
    @DisplayName("Get orders should return valid data")
    public void get_orders_should_return_valid_data() {
        String jwtToken = authClient.getJwtToken(
            TestConfig.getTestUsername(),
            TestConfig.getTestPassword()
        );

        Response response = orderClient.getOrdersRaw(jwtToken);
        response.then().statusCode(200);

        List<Order> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotNull();

        if (!orders.isEmpty()) {
            Order order = orders.get(0);
            assertThat(order.orderId()).isNotNull();
            assertThat(order.clientId()).isNotNull();
            assertThat(order.orderStatus()).isNotNull();
            assertThat(order.date()).isNotNull();

            System.out.println("✓ Orders fetched successfully. Count: " + orders.size());
        }
    }

    @Test
    @DisplayName("Get order by id should return valid data")
    public void get_order_by_id_should_return_valid_data() {
        String jwtToken = authClient.getJwtToken(
            TestConfig.getTestUsername(),
            TestConfig.getTestPassword()
        );

        List<Order> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();

        String orderId = orders.get(0).orderId();

        Response response = orderClient.getOrderByIdRaw(orderId, jwtToken);
        response.then().statusCode(200);

        Order order = orderClient.getOrderById(orderId, jwtToken);
        assertThat(order).isNotNull();
        assertThat(order.orderId()).isEqualTo(orderId);
        assertThat(order.clientId()).isNotNull();
        assertThat(order.orderStatus()).isNotNull();
        assertThat(order.date()).isNotNull();

        System.out.println("✓ Order fetched by ID: " + orderId);
    }

    @Test
    @DisplayName("Get order items should return valid data")
    public void get_order_items_should_return_valid_data() {
        String jwtToken = authClient.getJwtToken(
            TestConfig.getTestUsername(),
            TestConfig.getTestPassword()
        );

        List<Order> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();

        String orderId = orders.get(0).orderId();

        Response response = orderClient.getOrderItemsRaw(orderId, jwtToken);
        response.then().statusCode(200);

        List<OrderItem> items = orderClient.getOrderItems(orderId, jwtToken);
        assertThat(items).isNotNull();

        if (!items.isEmpty()) {
            OrderItem item = items.get(0);
            assertThat(item.itemId()).isNotNull();
            assertThat(item.quantity()).isGreaterThan(0);
//            assertThat(item.priceAtTimeOfOrder()).isNotNull();

            System.out.println("✓ Order items fetched. Count: " + items.size());
        }
    }

    @Test
    @DisplayName("Get order status should return valid data")
    public void get_order_status_should_return_valid_data() {
        String jwtToken = authClient.getJwtToken(
            TestConfig.getTestUsername(),
            TestConfig.getTestPassword()
        );

        List<Order> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();

        String orderId = orders.get(0).orderId();

        Response response = orderClient.getOrderStatusRaw(orderId, jwtToken);
        response.then().statusCode(200);

        OrderStatusResponse status = orderClient.getOrderStatus(orderId, jwtToken);
        assertThat(status).isNotNull();

        System.out.println("✓ Order status fetched: " + status);
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("\nOrder E2E tests completed!");
    }
}
