import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class longRedirectEx7 {

    /*
    Необходимо написать тест, который создает GET-запрос
    на адрес из предыдущего задания: https://playground.learnqa.ru/api/long_redirect

    На самом деле этот URL ведет на другой,
    который мы должны были узнать на предыдущем занятии.
    Но этот другой URL тоже куда-то редиректит. И так далее.
    Мы не знаем заранее количество всех редиректов и итоговый адрес.

    Наша задача — написать цикл, которая будет создавать запросы в цикле,
    каждый раз читая URL для редиректа из нужного заголовка.
    И так, пока мы не дойдем до ответа с кодом 200.
   */

    @Test
    public void longRedirect(){
        int countOfRedirect = 0;
        int status = -1;

        String Url = "https://playground.learnqa.ru/api/long_redirect";

        do {
            Response response = RestAssured
                     .given()
                     .redirects()
                     .follow(false)
                     .when()
                     .get(Url)
                     .andReturn();

             status = response.getStatusCode();
             Url = response.getHeader("Location");
             countOfRedirect++;
        } while ( status != 200 );

        System.out.println(countOfRedirect-1);
    }
}
