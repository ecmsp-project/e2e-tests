package com.ecmsp.e2e.tests;

import com.ecmsp.e2e.client.AuthClient;
import com.ecmsp.e2e.client.OrderClient;
import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.order.CreateOrderItemDto;
import com.ecmsp.e2e.dto.order.CreateOrderRequestDto;
import com.ecmsp.e2e.dto.order.CreateOrderResponseDto;
import com.ecmsp.e2e.dto.order.GetOrderResponseDto;
import com.ecmsp.e2e.dto.order.GetOrderItemDetailsDto;
import com.ecmsp.e2e.dto.order.GetOrderStatusResponseDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Order E2E Tests")
public class OrderE2ETest {

    private static AuthClient authClient;
    private static OrderClient orderClient;

    private static final String TEST_ITEM_ID = "test-item-123";
    private static final String TEST_VARIANT_ID = "test-variant-456";
    private static final String TEST_PRODUCT_NAME = "Test Product";
    private static final String TEST_IMAGE_URL = "http://example.com/image.jpg";
    private static final String TEST_DESCRIPTION = "Test product description";

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

        List<GetOrderResponseDto> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotNull();

        if (!orders.isEmpty()) {
            GetOrderResponseDto order = orders.get(0);
            assertThat(order.orderId()).isNotNull();
            assertThat(order.orderStatus()).isNotNull();
            assertThat(order.date()).isNotNull();
            assertThat(order.items()).isNotNull();

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

        List<GetOrderResponseDto> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();

        String orderId = orders.get(0).orderId();

        Response response = orderClient.getOrderByIdRaw(orderId, jwtToken);
        response.then().statusCode(200);

        GetOrderResponseDto order = orderClient.getOrderById(orderId, jwtToken);
        assertThat(order).isNotNull();
        assertThat(order.orderId()).isEqualTo(orderId);
        assertThat(order.orderStatus()).isNotNull();
        assertThat(order.date()).isNotNull();
        assertThat(order.items()).isNotNull();

        System.out.println("✓ Order fetched by ID: " + orderId);
    }

    @Test
    @DisplayName("Get order items should return valid data")
    public void get_order_items_should_return_valid_data() {
        String jwtToken = authClient.getJwtToken(
            TestConfig.getTestUsername(),
            TestConfig.getTestPassword()
        );

        List<GetOrderResponseDto> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();

        String orderId = orders.get(0).orderId();

        Response response = orderClient.getOrderItemsRaw(orderId, jwtToken);
        response.then().statusCode(200);

        List<GetOrderItemDetailsDto> items = orderClient.getOrderItems(orderId, jwtToken);
        assertThat(items).isNotNull();

        if (!items.isEmpty()) {
            GetOrderItemDetailsDto item = items.get(0);
            assertThat(item.itemId()).isNotNull();
            assertThat(item.variantId()).isNotNull();
            assertThat(item.quantity()).isGreaterThan(0);
            assertThat(item.price()).isGreaterThanOrEqualTo(0);
            assertThat(item.imageUrl()).isNotNull();
            assertThat(item.description()).isNotNull();

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

        List<GetOrderResponseDto> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();

        String orderId = orders.get(0).orderId();

        Response response = orderClient.getOrderStatusRaw(orderId, jwtToken);
        response.then().statusCode(200);

        GetOrderStatusResponseDto status = orderClient.getOrderStatus(orderId, jwtToken);
        assertThat(status).isNotNull();
        assertThat(status.orderId()).isNotNull();
        assertThat(status.orderStatus()).isNotNull();

        System.out.println("✓ Order status fetched: " + status);
    }

    @Test
    @DisplayName("Should create order successfully")
    public void should_create_order_successfully() {
        // Step 1: Authenticate
        String jwtToken = authClient.getJwtToken(
            TestConfig.getTestUsername(),
            TestConfig.getTestPassword()
        );

        // Step 2: Prepare request data
        CreateOrderItemDto item = new CreateOrderItemDto(
            TEST_ITEM_ID,
            TEST_VARIANT_ID,
            TEST_PRODUCT_NAME,
            2,
            29.99,
            TEST_IMAGE_URL,
            TEST_DESCRIPTION,
            true
        );

        CreateOrderRequestDto request = new CreateOrderRequestDto(
            List.of(item)
        );

        // Step 3: Call raw method first to check status
        Response response = orderClient.createOrderRaw(request, jwtToken);
        response.then().statusCode(201);

        // Step 4: Call typed method to get parsed response
        CreateOrderResponseDto createOrderResponse = orderClient.createOrder(request, jwtToken);

        // Step 5: Assertions
        assertThat(createOrderResponse).isNotNull();
        assertThat(createOrderResponse.isSuccess()).isTrue();
        assertThat(createOrderResponse.orderId()).isNotNull().isNotEmpty();
        assertThat(createOrderResponse.reservedVariantIds()).isNotNull();
        assertThat(createOrderResponse.reservedVariantIds()).contains(TEST_VARIANT_ID);

        // Step 6: Logging
        System.out.println("✓ Order created successfully");
        System.out.println("Order ID: " + createOrderResponse.orderId());
        System.out.println("Reserved variants: " + createOrderResponse.reservedVariantIds());
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("\nOrder E2E tests completed!");
    }
}
