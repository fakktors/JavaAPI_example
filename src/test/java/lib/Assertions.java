package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response response, String name, int expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertResponseTestEquals(Response Response, String expectedAnswer) {
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Response text is not answer"
        );
    }

    public static void assertResponseTextEquals(Response response, String expectedAnswer) {
        assertEquals(expectedAnswer,
                response.asString(),
                "Response text is not as expected");
    }

    public static void assertResponseCodeEquals(Response Response, Integer expectedStatusCode) {
        assertEquals(
                expectedStatusCode,
                Response.statusCode(),
                "Response status code is not expected"
        );
    }

    public static void assertJsonHasKey(Response Response, String expectedFieldName) {
        Response.then().assertThat().body("$", hasKey(expectedFieldName));
    }

    public static void assertJsonHasFieldsKey(Response Response, String[] expectedFieldNames) {
        for (String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasKey(Response, expectedFieldName);
        }
    }

    public static void assertJsonHasNotKey(Response Response, String unexpectedFieldName) {
        Response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }
}
