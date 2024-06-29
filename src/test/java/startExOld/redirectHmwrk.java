package startExOld;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class redirectHmwrk {

    /*
    Необходимо написать тест,
    который создает GET-запрос на адрес: https://playground.learnqa.ru/api/long_redirect

    С этого адреса должен происходит редирект на другой адрес.
    Наша задача — распечатать адрес, на который редиректит указанные URL.
     */

    @Test
    public void redirectHmwrk(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }
}
