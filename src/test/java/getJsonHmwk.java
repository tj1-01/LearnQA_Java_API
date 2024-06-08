import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class getJsonHmwk {

    /*
    В рамках этой задачи нужно создать тест,
    который будет делать GET-запрос на адрес
    https://playground.learnqa.ru/api/get_json_homework

    Полученный JSON необходимо распечатать и изучить.
    Мы увидим, что это данные с сообщениями и временем,
    когда они были написаны.
    Наша задача вывести текст второго сообщения.
     */

    @Test
    public void testGetJsonHmwk(){
        JsonPath responce = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        List<Map<String, Objects>> messages = responce.get("messages");
        if (messages.isEmpty()){
            System.out.println("Вернулся пустой список");
        } else if (messages.size()<2) {
            System.out.println("Нет нужного элемента");
        } else {
            String message = messages.get(1).toString();;
            System.out.println(message.substring(message.indexOf('=') + 1, message.indexOf(',')));
        }
    }
}