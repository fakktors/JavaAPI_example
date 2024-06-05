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

import java.util.HashMap;
import java.util.Map;

@Epic("Edit cases")
@Feature("Editing")
public class UserEditTest extends BaseTestCase {

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    String url = "https://playground.learnqa.ru/api/user/";

    @Test
    @Description("Cannot change data if not authorized")
    @Epic("User Editing")
    @DisplayName("Attempt to change user data while not authorized")
    void testEditWithoutAuth() {
        String newName = "new name";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        JsonPath responseEditUser = apiCoreRequests
                .putEditUserJsonRequest(url, "2",
                        "", "", editData);

        Assertions.assertJsonTextEquals(responseEditUser, "error", "Auth token not supplied");

        Response userDataResponse = apiCoreRequests
                .makeGetRequest(url, "2", "", "");

        Assertions.assertJsonByName(userDataResponse, "username", "Vitaliy");
    }

    @Test
    @Description("Cannot change user data if the user does not belong to you")
    @Epic("User Editing")
    @DisplayName("Attempt to change user data while authenticated as another user")
    void testEditWithAnotherUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        apiCoreRequests.generateUserRequest(url, userData);

        Response responseGetAuthForEdit = apiCoreRequests
                .authRequest("vinkotov@example.com", "1234");

        Response responseGetAuthAnother = apiCoreRequests
                .authRequest(userData.get("email"), userData.get("password"));

        String newName = "Change name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        apiCoreRequests
                .putEditUserRequest(url, "2",
                        this.getHeader(responseGetAuthAnother, "x-csrf-token"),
                        this.getCookie(responseGetAuthAnother, "auth_sid"),
                        editData);

        Response userDataResponse = apiCoreRequests
                .makeGetRequest(url,
                        "2",
                        this.getHeader(responseGetAuthForEdit, "x-csrf-token"),
                        this.getCookie(responseGetAuthForEdit, "auth_sid"));

        Assertions.assertJsonByName(userDataResponse, "firstName", "Vitalii");
    }

    @Test
    @Description("Negative validation check for the email field when changing your data")
    @Epic("User Editing")
    @DisplayName("Attempt to change the user's email while authenticated as the same user to a new email without the @ symbol")
    void testEditWithWrongEmail() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .generateUserRequest(url, userData);

        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");

        Response responseGetAuth = apiCoreRequests
                .authRequest(userEmail, userData.get("password"));

        Map<String, String> editData = new HashMap<>();
        editData.put("email", "test.test.ru");

        JsonPath responseEditUser = apiCoreRequests
                .putEditUserJsonRequest(url, userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        Assertions.assertJsonTextEquals(responseEditUser, "error", "Invalid email format");

        Response userDataResponse = apiCoreRequests
                .makeGetRequest(url, userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(userDataResponse, "email", userEmail);
    }

    @Test
    @Description("Negative validation check for changing the user's first name to a short name")
    @Epic("User Editing")
    @DisplayName("Attempt to change the user's first name while authenticated as the same user to a very short value of one character")
    void testEditFirstNameTooShort() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests
                .generateUserRequest(url, userData);

        String userId = responseCreateAuth.getString("id");

        Response responseGetAuth = apiCoreRequests
                .authRequest(userData.get("email"), userData.get("password"));

        String newName = "A";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .putEditUserRequest(url, userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        Assertions.assertJsonByName(responseEditUser, "error", "The value for field `firstName` is too short");

        Response responseUserData = apiCoreRequests
                .makeGetRequest(url, userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }
}
