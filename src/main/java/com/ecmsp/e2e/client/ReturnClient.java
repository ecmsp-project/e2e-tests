package com.ecmsp.e2e.client;

import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.returns.CreateReturnResponseDto;
import com.ecmsp.e2e.dto.returns.ReturnOrder;
import com.ecmsp.e2e.dto.returns.ReturnToCreate;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ReturnClient {
    private final String gatewayUrl;

    public ReturnClient() {
        this.gatewayUrl = TestConfig.getGatewayUrl();
        RestAssured.baseURI = gatewayUrl;
    }

    public CreateReturnResponseDto createReturn(ReturnToCreate returnToCreate, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .body(returnToCreate)
            .when()
            .post("/api/returns")
            .then()
            .statusCode(201)
            .extract()
            .response();

        return response.as(CreateReturnResponseDto.class);
    }

    public Response createReturnRaw(ReturnToCreate returnToCreate, String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .body(returnToCreate)
            .when()
            .post("/api/returns");
    }

    public List<ReturnOrder> getReturns(String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/returns")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return Arrays.asList(response.as(ReturnOrder[].class));
    }

    public Response getReturnsRaw(String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/returns");
    }

    public ReturnOrder getReturn(String returnId, String jwtToken) {
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/returns/" + returnId)
            .then()
            .statusCode(200)
            .extract()
            .response();

        return response.as(ReturnOrder.class);
    }

    public Response getReturnRaw(String returnId, String jwtToken) {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + jwtToken)
            .when()
            .get("/api/returns/" + returnId);
    }
}
