package com.ecmsp.e2e.client;

import com.ecmsp.e2e.config.TestConfig;
import com.ecmsp.e2e.dto.login.LoginRequest;
import com.ecmsp.e2e.dto.login.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {
    private final String gatewayUrl;
    
    public AuthClient() {
        this.gatewayUrl = TestConfig.getGatewayUrl();
        RestAssured.baseURI = gatewayUrl;
    }


    public LoginResponse login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .response();
        
        return response.as(LoginResponse.class);
    }

    public Response loginRaw(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);

        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/auth/login");
    }

    
    public String getJwtToken(String username, String password) {
        LoginResponse response = login(username, password);
        return response.token();
    }
}