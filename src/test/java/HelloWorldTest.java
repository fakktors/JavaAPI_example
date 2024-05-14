import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HelloWorldTest {

    @Test
    public void testRestAssured(){
        Response response = RestAssured
                .given()
                .queryParam("name", "John")
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testEx5(){
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String answer = response.get("messages[1].message");
        System.out.println(answer);
    }

    @Test
    public void testEx6(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String location = response.getHeader("Location");
        System.out.println(location);
    }

    @Test
    public void testEx7(){
        int redirectCount = 0;
        int statusCode = 0;
        String link = "https://playground.learnqa.ru/api/long_redirect";
        
        while (statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(link)
                    .andReturn();

            statusCode = response.getStatusCode();
            link = response.getHeader("Location");
            if(statusCode == 301){
                redirectCount++;
            }
        }

        System.out.println("Код ответа: " + statusCode);
        System.out.println("Количество редиректов: " + redirectCount);
    }

    @Test
    public void testEx8() throws InterruptedException {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = response.get("token");
        int seconds = response.get("seconds");

        JsonPath responseStatusError = RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String statusNotReady = responseStatusError.get("status");

        if(statusNotReady.equals("Job is NOT ready")){
            TimeUnit.SECONDS.sleep(seconds);

            JsonPath responseStatusSuccess = RestAssured
                    .given()
                    .queryParam("token", token)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();

            String status = responseStatusSuccess.get("status");

            if(status.equals("Job is ready")){
                System.out.println(status);
            }
            else {
                System.out.println(statusNotReady);
            }
        }
    }

    @Test
    public void testEx9()  {
        String getPassword = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String checkCookie = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

        List<String> passwordList = Arrays.asList(
                "password","dragon","baseball","111111","iloveyou","master","sunshine",
                "ashley","bailey","passw0rd","shadow","123456","123123","654321","superman","qazwsx","michael",
                "football","12345678","qwerty","abc123","monkey","1234567","letmein","trustno1","jesus","ninja",
                "mustang","password1","adobe123","admin","1234567890","photoshop[a]","1234","12345","princess",
                "azerty","0","123456789","access","696969","batman","1qaz2wsx","login","qwertyuiop","solo","starwars",
                "121212","flower","hottie","loveme","zaq1zaq1","hello","freedom","whatever","666666","!@#$%^&*",
                "charlie","aa123456","donald","qwerty123","1q2w3e4r","555555","lovely","7777777","welcome","888888",
                "123qwe","iloveyou"
        );

        Map<String, String> body = new HashMap<>();

        for (String password : passwordList) {
            body.put("login", "super_admin");
            body.put("password", password);


            Response responseToken = RestAssured
                    .given()
                    .body(body)
                    .post(getPassword)
                    .andReturn();

            String responseCookie = responseToken.getCookie("auth_cookie");

            Map<String, String> cookie = new HashMap<>();
            cookie.put("auth_cookie", responseCookie);

            Response responseCheck = RestAssured
                    .given()
                    .cookies(cookie)
                    .get(checkCookie)
                    .andReturn();

            String checkMessage = responseCheck.asString();

            if (checkMessage.equals("You are authorized")) {
                System.out.println("You are authorized");
                System.out.println("Correct password - " + password);
                System.out.println(responseCheck.getStatusCode());
            }
        }
    }
}
