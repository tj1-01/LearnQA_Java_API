package startExOld;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class cookieTestEx11 {

    @Test
    public void findCokkie(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        assertEquals("I find cookie", response.cookies(),"Тест падает, но показывает куки:");
    }

    @Test
    public void cookieTestEx11(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        //проверка куки и значения в ней
        assertEquals("hw_value", response.getCookie("HomeWork"),"Неожиданный набор Cookie и значения");
    }
}
