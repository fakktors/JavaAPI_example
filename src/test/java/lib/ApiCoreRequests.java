package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-request with id")
    public Response makeGetRequest(String url, String id) {
        return given()
                .filter(new AllureRestAssured())
                .get(url + id)
                .andReturn();
    }

    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with id, token and auth cookie")
    public Response makeGetRequest(String url, String id, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url + id)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request")
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a put-request with token")
    public Response putEditUserRequest(String url, String userId, String header, String cookie,Map<String, String> editData) {
        return
                given()
                        .header("x-csrf-token", header)
                        .cookie("auth_sid", cookie)
                        .body(editData)
                        .put(url + userId)
                        .andReturn();
    }

    @Step("Make a put-request jsonPath with token")
    public JsonPath putEditUserJsonRequest(String url, String userId, String header, String cookie,Map<String, String> editData) {
        return
                given()
                        .header("x-csrf-token", header)
                        .cookie("auth_sid", cookie)
                        .body(editData)
                        .put(url + userId)
                        .jsonPath();
    }

    @Step("Make a delete-request with token")
    public Response deleteUserRequest(String url, String userId, String header, String cookie) {
        return  given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .delete(url + userId)
                .andReturn();
    }

    @Step("Make a delete-request with token")
    public JsonPath deleteUserJsonRequest(String url, String userId, String header, String cookie) {
        return  given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .delete(url + userId)
                .jsonPath();
    }

    @Step("Make new user")
    public JsonPath generateUserRequest(String url, Map<String, String> userData) {
        return  given()
                .body(userData)
                .post(url)
                .jsonPath();
    }


    @Step("AuthRequest")
    public Response authRequest(String email, String password) {
        Map<String, String> authDate = new HashMap<>();
        authDate.put("email", email);
        authDate.put("password", password);

        return  given()
                .body(authDate)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
    }
}
