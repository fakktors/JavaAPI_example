package org.example.tests;

import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;

import java.util.HashMap;
import java.util.Map;

@Epic("Get cases")
@Feature("Data Retrieval")
public class UserGetTest extends BaseTestCase {

    String url = "https://playground.learnqa.ru/api/user/";

    String urlLogin = "https://playground.learnqa.ru/api/user/login";

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Attempt to get user data without authorization")
    @Epic("User Data Retrieval")
    @DisplayName("Get user data without authorization")
    public void testGetUserDataNoAuth() {
        Response responseUserData = apiCoreRequests
                .makeGetRequest(url, "2");

        Assertions.assertJsonHasKey(responseUserData, "username");
        Assertions.assertJsonHasNotKey(responseUserData, "firstName");
        Assertions.assertJsonHasNotKey(responseUserData, "lastName");
        Assertions.assertJsonHasNotKey(responseUserData, "email");
    }

    @Test
    @Description("Get user details while authenticated as the same user")
    @Epic("User Data Retrieval")
    @DisplayName("Get user details as the same user")
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, authData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseUserData = apiCoreRequests
                .makeGetRequest(url, "2", header, cookie);

        String[] expectedFields = {"username", "lastName", "firstName", "email"};

        Assertions.assertJsonHasFieldsKey(responseUserData, expectedFields);
    }

    @Test
    @Description("Get user details while authenticated as any user")
    @Epic("User Data Retrieval")
    @DisplayName("Get user details as any user")
    public void testGetUserDetailsAuthAsAnyUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, authData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseUserData = apiCoreRequests
                .makeGetRequest(url, "1", header, cookie);

        Assertions.assertJsonHasKey(responseUserData, "username");
        Assertions.assertJsonHasNotKey(responseUserData, "firstName");
        Assertions.assertJsonHasNotKey(responseUserData, "lastName");
        Assertions.assertJsonHasNotKey(responseUserData, "email");
    }
}
