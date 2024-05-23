import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCasesExample {

    String urlLongMethod = "https://playground.learnqa.ru/api/hello";

    @Test
    public void testLongMethod(){

        JsonPath response = RestAssured
                .given()
                .queryParam("name", "Johny Production")
                .get(urlLongMethod)
                .jsonPath();

        String envString = response.get("answer");
        assertTrue(envString.length() > 15, "String length is not greater than 15 characters");
    }
}
