import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class longstringEx10 {

    @Test
    public void longstringEx10(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        //"Hello, world" (12 символов)
        assertTrue(response.getBody().asString().length() > 15, "String is too short.");
    }
}