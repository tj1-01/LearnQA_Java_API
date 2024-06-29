package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {

    public static void assertJsonByName(Response Response, String name, int expectedValues){
        Response.then().assertThat().body("$", hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValues, value, "Значение в JSON отличается от ожидаемого");
    }

    public static void assertJsonByName(Response Response, String name, String expectedValues){
        Response.then().assertThat().body("$", hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValues, value, "Значение в JSON отличается от ожидаемого");
    }

     public static void assertResponseTextEquals(Response response, String expectedAnswer){
        assertEquals(
                expectedAnswer,
                response.asString(),
                "Текст в response отличается от ожидаемого"
        );
     }

    public static void assertResponseCodeEquals(Response response, int codeExpected){
        assertEquals(
                codeExpected,
                response.statusCode(),
                "Код ответа отличается от ожидаемого"
        );
    }

     public static void assertJsonHasField(Response response, String expectedFieldName){
        response.then().assertThat().body("$", hasKey(expectedFieldName));
     }

     public static void assertJsonHasNotField(Response response, String unexpectedFielsName){
        response.then().assertThat().body("$", not(hasKey(unexpectedFielsName)));
     }

     public static void assertJsonHasFields(Response response, String[] expectedFieldNames) {
        for (String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasField(response, expectedFieldName);
        }
     }


}
