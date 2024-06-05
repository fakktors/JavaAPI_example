package org.example.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

@Epic("Delete cases")
@Feature("Deletion")
public class UserDeleteTest extends BaseTestCase {

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    String url = "https://playground.learnqa.ru/api/user/";

    @Test
    @Description("The test attempts to delete a user who does not meet the deletion criteria")
    @Epic("User Deletion")
    @DisplayName("Delete user by ID 2")
    void testDeleteUser() {
        Response responseGetAuth = apiCoreRequests
                .authRequest("vinkotov@example.com", "1234");

        JsonPath responseDelete = apiCoreRequests
                .deleteUserJsonRequest(url, "2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonTextEquals(responseDelete, "error", "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("The test successfully deletes a newly created user")
    @Epic("User Deletion")
    @DisplayName("Delete created user")
    void testDeleteCreatedUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .generateUserRequest(url, userData);

        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");

        Response responseGetAuth = apiCoreRequests
                .authRequest(userEmail, userData.get("password"));

        apiCoreRequests
                .deleteUserRequest(url, userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Response userDataResponse = apiCoreRequests
                .makeGetRequest(url, userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseTextEquals(userDataResponse, "User not found");
    }

    @Test
    @Description("The test checks an attempt to delete a user who does not belong to the authorized user")
    @Epic("User Deletion")
    @DisplayName("Attempt to delete a user while authenticated as another user")
    void testDeleteAnotherUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .generateUserRequest(url, userData);

        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");

        Response responseSecondGetAuth = apiCoreRequests
                .authRequest("vinkotov@example.com", "1234");

        JsonPath responseDelete = apiCoreRequests
                .deleteUserJsonRequest(url, userId,
                        this.getHeader(responseSecondGetAuth, "x-csrf-token"),
                        this.getCookie(responseSecondGetAuth, "auth_sid"));

        Assertions.assertJsonTextEquals(responseDelete, "error", "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        Response responseGetAuth = apiCoreRequests
                .authRequest(userEmail, userData.get("password"));

        Response userDataResponse = apiCoreRequests
                .makeGetRequest(url, userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(userDataResponse, "id", userId);
        Assertions.assertJsonByName(userDataResponse, "username", userData.get("username"));
        Assertions.assertJsonByName(userDataResponse, "email", userData.get("email"));
        Assertions.assertJsonByName(userDataResponse, "firstName", userData.get("firstName"));
        Assertions.assertJsonByName(userDataResponse, "lastName", userData.get("lastName"));
    }
}
