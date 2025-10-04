package com.ecmsp.e2e.client;

import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.order.Order;
import com.ecmsp.e2e.dto.order.OrderItem;
import com.ecmsp.e2e.dto.order.OrderStatusResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private final String gatewayUrl;
    
    public OrderClient() {
        this.gatewayUrl = TestConfig.getGatewayUrl();
        RestAssured.baseURI = gatewayUrl;
    }
    
    public List<Order> getOrdersViaRest(String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return Arrays.asList(response.as(Order[].class));
    }
    
    public Response getOrdersViaRestRaw(String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders");
    }

    public List<Order> getOrders(String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return Arrays.asList(response.as(Order[].class));
    }

    public Response getOrdersRaw(String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc");
    }

    public Order getOrderById(String orderId, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId)
            .then()
            .statusCode(200)
            .extract()
            .response();

        return response.as(Order.class);
    }

    public Response getOrderByIdRaw(String orderId, String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId);
    }

    public List<OrderItem> getOrderItems(String orderId, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId + "/items")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return Arrays.asList(response.as(OrderItem[].class));
    }

    public Response getOrderItemsRaw(String orderId, String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId + "/items");
    }

    public OrderStatusResponse getOrderStatus(String orderId, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId + "/status")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return response.as(OrderStatusResponse.class);
    }

    public Response getOrderStatusRaw(String orderId, String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId + "/status");
    }
}