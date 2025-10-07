package com.ecmsp.e2e.client;

import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.order.GetOrderResponseDto;
import com.ecmsp.e2e.dto.order.GetOrderItemDetailsDto;
import com.ecmsp.e2e.dto.order.GetOrderStatusResponseDto;
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
    
    public List<GetOrderResponseDto> getOrdersViaRest(String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return Arrays.asList(response.as(GetOrderResponseDto[].class));
    }
    
    public Response getOrdersViaRestRaw(String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders");
    }

    public List<GetOrderResponseDto> getOrders(String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return Arrays.asList(response.as(GetOrderResponseDto[].class));
    }

    public Response getOrdersRaw(String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc");
    }

    public GetOrderResponseDto getOrderById(String orderId, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId)
            .then()
            .statusCode(200)
            .extract()
            .response();

        return response.as(GetOrderResponseDto.class);
    }

    public Response getOrderByIdRaw(String orderId, String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId);
    }

    public List<GetOrderItemDetailsDto> getOrderItems(String orderId, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId + "/items")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return Arrays.asList(response.as(GetOrderItemDetailsDto[].class));
    }

    public Response getOrderItemsRaw(String orderId, String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId + "/items");
    }

    public GetOrderStatusResponseDto getOrderStatus(String orderId, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId + "/status")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return response.as(GetOrderStatusResponseDto.class);
    }

    public Response getOrderStatusRaw(String orderId, String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/grpc/" + orderId + "/status");
    }
}