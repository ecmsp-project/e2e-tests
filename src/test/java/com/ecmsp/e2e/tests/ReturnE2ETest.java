package com.ecmsp.e2e.tests;

import com.ecmsp.e2e.client.AuthClient;
import com.ecmsp.e2e.client.OrderClient;
import com.ecmsp.e2e.client.ReturnClient;
import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.order.GetOrderItemDetailsDto;
import com.ecmsp.e2e.dto.order.GetOrderResponseDto;
import com.ecmsp.e2e.dto.returns.CreateReturnResponseDto;
import com.ecmsp.e2e.dto.returns.ItemToReturnDetails;
import com.ecmsp.e2e.dto.returns.ReturnOrder;
import com.ecmsp.e2e.dto.returns.ReturnToCreate;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Return E2E Tests")
public class ReturnE2ETest {

    private AuthClient authClient;
    private OrderClient orderClient;
    private ReturnClient returnClient;

    private static final String VARIANT_ID_PLACEHOLDER = "83869791-182f-4eca-aeea-f434d279b2ee";

    @BeforeEach
    public void setUp() {
        authClient = new AuthClient();
        orderClient = new OrderClient();
        returnClient = new ReturnClient();
    }

    private String getAuthToken() {
        return authClient.getJwtToken(
            TestConfig.getTestUsername(),
            TestConfig.getTestPassword()
        );
    }

    @Test
    @DisplayName("Should create return for existing order")
    public void should_create_return_for_existing_order() {
        // Get auth jwtToken
        String jwtToken = getAuthToken();

        // Get user's orders
        List<GetOrderResponseDto> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();

        GetOrderResponseDto firstOrder = orders.get(0);
        assertThat(firstOrder.orderId()).isNotNull();
        assertThat(firstOrder.orderStatus()).isNotNull();
        assertThat(firstOrder.date()).isNotNull();
        assertThat(firstOrder.items()).isNotEmpty();

        // Create return for first order
        GetOrderItemDetailsDto firstItem = firstOrder.items().get(0);
        assertThat(firstItem.itemId()).isNotNull();
        assertThat(firstItem.variantId()).isNotNull();
        assertThat(firstItem.quantity()).isGreaterThan(0);
        assertThat(firstItem.price()).isGreaterThanOrEqualTo(0);
        assertThat(firstItem.imageUrl()).isNotNull();
        assertThat(firstItem.description()).isNotNull();

        String itemId = firstItem.itemId();

        ItemToReturnDetails itemToReturn = new ItemToReturnDetails(
            itemId,
            VARIANT_ID_PLACEHOLDER,
            1,
            "Defective product"
        );

        ReturnToCreate returnToCreate = new ReturnToCreate(
            firstOrder.orderId(),
            List.of(itemToReturn)
        );

        // Create return
        Response response = returnClient.createReturnRaw(returnToCreate, jwtToken);
        response.then().statusCode(201);

        CreateReturnResponseDto createReturnResponse = returnClient.createReturn(returnToCreate, jwtToken);

        // Verify return
        assertThat(createReturnResponse).isNotNull();
        assertThat(createReturnResponse.returnId()).isNotNull().isNotEmpty();
        assertThat(createReturnResponse.returnStatus()).isNotNull().isNotEmpty();


        System.out.println("✓ Return created successfully");
        System.out.println("Return ID: " + createReturnResponse.returnId());
        System.out.println("Status: " + createReturnResponse.returnStatus());
    }

    @Test
    @DisplayName("Should get all returns for user")
    public void should_get_all_returns_for_user() {
        // Get auth jwtToken
        String jwtToken = getAuthToken();

        // Fetch all returns
        Response response = returnClient.getReturnsRaw(jwtToken);
        response.then().statusCode(200);

        List<ReturnOrder> returns = returnClient.getReturns(jwtToken);

        // Verify returns
        assertThat(returns).isNotNull();

        System.out.println("✓ Returns fetched successfully");
        System.out.println("Number of returns: " + returns.size());

        if (!returns.isEmpty()) {
            ReturnOrder firstReturn = returns.get(0);
            assertThat(firstReturn.returnId()).isNotNull();
            assertThat(firstReturn.orderId()).isNotNull();
            assertThat(firstReturn.status()).isNotNull();
            assertThat(firstReturn.itemsToReturn()).isNotNull();

            System.out.println("First Return ID: " + firstReturn.returnId());
        }
    }

    @Test
    @DisplayName("Should get specific return by ID")
    public void should_get_specific_return_by_id() {
        // Get auth jwtToken
        String jwtToken = getAuthToken();

        // First, create a return
        List<GetOrderResponseDto> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();

        GetOrderResponseDto firstOrder = orders.get(0);
        assertThat(firstOrder.orderId()).isNotNull();
        assertThat(firstOrder.orderStatus()).isNotNull();
        assertThat(firstOrder.date()).isNotNull();
        assertThat(firstOrder.items()).isNotEmpty();

        String itemId = firstOrder.items().get(0).itemId();

        ItemToReturnDetails itemToReturn = new ItemToReturnDetails(
            itemId,
            VARIANT_ID_PLACEHOLDER,
            1,
            "Changed mind"
        );

        ReturnToCreate returnToCreate = new ReturnToCreate(
            firstOrder.orderId(),
            List.of(itemToReturn)
        );

        CreateReturnResponseDto createdReturn = returnClient.createReturn(returnToCreate, jwtToken);
        String returnId = createdReturn.returnId();

        // Fetch the specific return
        Response response = returnClient.getReturnRaw(returnId, jwtToken);
        response.then().statusCode(200);

        ReturnOrder fetchedReturn = returnClient.getReturn(returnId, jwtToken);

        // Verify return
        assertThat(fetchedReturn).isNotNull();
        assertThat(fetchedReturn.returnId()).isEqualTo(returnId);
        assertThat(fetchedReturn.orderId()).isEqualTo(firstOrder.orderId());
        assertThat(fetchedReturn.itemsToReturn()).hasSize(0);
        assertThat(fetchedReturn.status()).isNotNull();

        System.out.println("✓ Specific return fetched successfully");
        System.out.println("Return ID: " + fetchedReturn.returnId());
        System.out.println("Order ID: " + fetchedReturn.orderId());
    }

    @Test
    @DisplayName("Should fail to create return without authentication")
    public void should_fail_to_create_return_without_authentication() {
        // Create return request
        ItemToReturnDetails itemToReturn = new ItemToReturnDetails(
            "item-123",
            VARIANT_ID_PLACEHOLDER,
            1,
            "Test reason"
        );

        ReturnToCreate returnToCreate = new ReturnToCreate(
            "order-123",
            List.of(itemToReturn)
        );

        // Try to create return without jwtToken
        Response response = returnClient.createReturnRaw(returnToCreate, "");

        assertThat(response.getStatusCode()).isIn(401, 403);

        System.out.println("✓ Unauthorized return creation properly blocked");
    }

    @Test
    @DisplayName("Should fail to get returns without authentication")
    public void should_fail_to_get_returns_without_authentication() {
        // Try to get returns without jwtToken
        Response response = returnClient.getReturnsRaw("");

        assertThat(response.getStatusCode()).isIn(401, 403);

        System.out.println("✓ Unauthorized returns access properly blocked");
    }

    @Test
    @DisplayName("Should fail to get specific return without authentication")
    public void should_fail_to_get_specific_return_without_authentication() {
        // Try to get return without jwtToken
        Response response = returnClient.getReturnRaw("some-return-id", "");

        assertThat(response.getStatusCode()).isIn(401, 403);

        System.out.println("✓ Unauthorized return access properly blocked");
    }

    @Test
    @DisplayName("Complete flow: Login, create return, and verify")
    public void complete_flow_login_create_return_and_verify() {
        // Step 1: Login
        String jwtToken = getAuthToken();
        assertThat(jwtToken).isNotNull().isNotEmpty();
        System.out.println("✓ Step 1: Login successful");

        // Step 2: Get user's orders
        List<GetOrderResponseDto> orders = orderClient.getOrders(jwtToken);
        assertThat(orders).isNotEmpty();
        System.out.println("✓ Step 2: Orders fetched");

        GetOrderResponseDto firstOrder = orders.get(0);
        assertThat(firstOrder.orderId()).isNotNull();
        assertThat(firstOrder.orderStatus()).isNotNull();
        assertThat(firstOrder.date()).isNotNull();
        assertThat(firstOrder.items()).isNotEmpty();

        // Step 3: Create return
        String itemId = firstOrder.items().get(0).itemId();

        ItemToReturnDetails itemToReturn = new ItemToReturnDetails(
            itemId,
            VARIANT_ID_PLACEHOLDER,
            1,
            "Complete flow test"
        );

        ReturnToCreate returnToCreate = new ReturnToCreate(
            firstOrder.orderId(),
            List.of(itemToReturn)
        );

        CreateReturnResponseDto createdReturn = returnClient.createReturn(returnToCreate, jwtToken);
        assertThat(createdReturn.returnId()).isNotNull();
        System.out.println("✓ Step 3: Return created");

        // Step 4: Verify return in user's returns list
        List<ReturnOrder> returns = returnClient.getReturns(jwtToken);
        assertThat(returns).isNotNull();
        assertThat(returns.stream()
            .anyMatch(r -> r.returnId().equals(createdReturn.returnId())))
            .isTrue();
        System.out.println("✓ Step 4: Return verified in user's returns list");

        // Step 5: Fetch specific return
        ReturnOrder fetchedReturn = returnClient.getReturn(createdReturn.returnId(), jwtToken);
        assertThat(fetchedReturn.returnId()).isEqualTo(createdReturn.returnId());
        assertThat(fetchedReturn.orderId()).isEqualTo(firstOrder.orderId());
        System.out.println("✓ Step 5: Specific return fetched and verified");

        System.out.println("✓ Complete E2E flow successful");
    }
}
