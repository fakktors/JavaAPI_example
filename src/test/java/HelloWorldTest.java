import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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
            redirectCount++;
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
}
