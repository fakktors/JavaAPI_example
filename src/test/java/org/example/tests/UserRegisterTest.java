package org.example.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Register cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    String url = "https://playground.learnqa.ru/api/user/";

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Successfully create a new user")
    @Epic("User register")
    @DisplayName("Create user successfully")
    public void testCreateUserSuccessfully() {
        Map<String, String> params = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, params);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasKey(responseCreateAuth, "id");
    }

    @ParameterizedTest
    @Description("Attempt to create a user without one of the required parameters")
    @Epic("User register")
    @ValueSource(strings = {"username", "email", "password", "firstName", "lastName"})
    @DisplayName("Create user without one of the required parameters")
    void testCreateUserWithoutOneOfParameters(String condition) {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        if (condition.equals("username") || condition.equals("email") ||
                condition.equals("password") || condition.equals("firstName") ||
                condition.equals("lastName")) {
            userData.remove(condition);
        } else {
            throw new IllegalArgumentException("Condition value is unknown: " + condition);
        }

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + condition);
    }

    @Test
    @Description("Attempt to create a user with an existing email")
    @Epic("User register")
    @DisplayName("Create user with an existing email")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", "1234");
        params.put("firstName", "testkotov");
        params.put("lastName", "testkotov");
        params.put("username", "testkotov");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, params);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTestEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("Attempt to create a user with an invalid email format")
    @Epic("User register")
    @DisplayName("Create user with an invalid email format")
    public void testCreateUserWithInvalidFormatEmail() {
        String email = "vinkotovexample.com";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", "1234");
        params.put("firstName", "testkotov");
        params.put("lastName", "testkotov");
        params.put("username", "testkotov");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, params);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTestEquals(responseCreateAuth, "Invalid email format");
    }

    @Test
    @Description("Attempt to create a user with a very short username")
    @Epic("User register")
    @DisplayName("Create user with a short username")
    public void testCreateUserWithShortUsername() {
        String email = "test@example.com";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", "1234");
        params.put("firstName", "testkotov");
        params.put("lastName", "testkotov");
        params.put("username", "t");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, params);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTestEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Test
    @Description("Attempt to create a user with a very long username")
    @Epic("User register")
    @DisplayName("Create user with a long username")
    public void testCreateUserWithLongUsername() {
        String email = "test@example.com";
        String usernameLong = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", "1234");
        params.put("firstName", "testkotov");
        params.put("lastName", "testkotov");
        params.put("username", usernameLong);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, params);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTestEquals(responseCreateAuth, "The value of 'username' field is too long");
    }
}
