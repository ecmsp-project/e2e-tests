package com.ecmsp.e2e.client;

import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.order.Order;
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
    
    public List<Order> getMyOrders(String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/me")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return Arrays.asList(response.as(Order[].class));
    }
    
    public Response getMyOrdersRaw(String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/me");
    }
    
    public Order getOrderById(String orderId, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/orders/" + orderId)
            .then()
            .statusCode(200)
            .extract()
            .response();
        
        return response.as(Order.class);
    }
}