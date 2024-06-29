package startExOld;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class tokensEx8 {

    /*
       На первый запрос API начинает выполнения задачи,
       на последующие ЛИБО говорит, что задача еще не готова, ЛИБО выдает результат.

       API-метод находится по следующему URL: https://playground.learnqa.ru/ajax/api/longtime_job

       Написать тест, который сделал бы следующее:
       1) создавал задачу
       2) делал один запрос с token ДО того, как задача готова,
          убеждался в правильности поля status
       3) ждал нужное количество секунд с помощью функции
          Thread.sleep() - для этого надо сделать import time
       4) делал бы один запрос c token ПОСЛЕ того, как задача готова,
          убеждался в правильности поля status и наличии поля result
    */

    @Test
    public void tokensEx8() throws InterruptedException {

        /*
        1. Создаем задачу, получаем token и время задачи
        */
        int mySec = 0;
        String myToken;
        String myResult;
        String statusProcess = "Job is NOT ready";
        String statusDone =  "Job is ready";

        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        mySec = response.get("seconds");
        myToken = response.get("token");

        if ( mySec != 0 && myToken != null) {
            System.out.println("Шаг1: Задача создана");
        } else {
            System.out.println("Шаг1: ошибка шага");
        }

        /*
        2. Делаем запроc до завершения задачи,
        проверяем status: ожидаем "Job is NOT ready"
         */
        JsonPath responseCheck01 = RestAssured
                .given()
                .queryParam("token", myToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        if (responseCheck01.get("status").equals(statusProcess)) {
            System.out.println("Шаг2: Тест отработал верно, задача ещё не готова.\n" +
                    "      Status: " + statusProcess);
        } else {
            System.out.println("Шаг2: ошибка шага");
        }

        /*
         3. Ждем ожидаемое время
         Умножаем на 1000 т.к. функция ждет значение в милисекундах
         */
        System.out.println("Шаг3: ожидаем время выполнения: " +  mySec + "сек");
        try {
            Thread.sleep(mySec*1000);
        } catch (InterruptedException e) {
            // Обработка исключения, если поток был прерван во время сна
            e.printStackTrace();
        }

        /*
         4. Проверяем что отработало
         */
        JsonPath responseCheck02 = RestAssured
                .given()
                .queryParam("token", myToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        myResult = responseCheck02.get("result");
        if ( myResult != null  ||  responseCheck02.get("status").equals(statusDone)) {
            System.out.println("Шаг4: Тест отработал верно, задача готова.\n" +
                    "      Результат: " + myResult + "\n"+
                    "      Status: " + statusDone);
        } else {
            System.out.println("Шаг4: ошибка шага");
        }
    }
}