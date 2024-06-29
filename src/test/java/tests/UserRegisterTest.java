package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Epic("Register User Cases")
@Feature("Creating")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String url = "https://playground.learnqa.ru/api/user/";

    @Test
    @Description("Тест успешно регестрирует нового пользователя")
    @DisplayName("Позитивный кейс авторизации")
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makedPostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("Тест проверяет невозможность зарегестрировать пользователя с задвоением email")
    @DisplayName("Ошибка регистрации при повторном e-mail")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makedPostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(
                responseCreateAuth,
                "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("Тест проверяет невозможность зарегестрировать пользователя с некорректным email")
    @DisplayName("Ошибка регистрации e-mail без знака @")
    public void testCreateUserWithBrokenEmail() {
        String email = DataGenerator.getRandomEmail().replace("@", ".");

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makedPostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(
                responseCreateAuth,
                "Invalid email format");
    }

    @Test
    @Description("Тест проверяет невозможность зарегестрировать пользователя с коротким именем")
    @DisplayName("Ошибка регистрации c длиной имени в 1 символ")
    public void testCreateUserShortName() {
        String name = "A";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makedPostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(
                responseCreateAuth,
                "The value of 'username' field is too short");
    }

    @Test
    @Description("Тест проверяет невозможность зарегестрировать пользователя с длинным именем")
    @DisplayName("Ошибка регистрации c длиной имени длиннее 250 символов")
    public void testCreateUserTooLongName() {
        //  Получим имя в 251 символ
        String name = "A";
        for (int i = 0; i < 250; i++) {
            name = name + "A";
        }

        Map<String, String> userData = new HashMap<>();
        userData.put("username", name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makedPostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(
                responseCreateAuth,
                "The value of 'username' field is too long");
    }

    @Description("Тест проверяет невозможность зарегестрировать пользователя без какого-либо поля")
    @DisplayName("Тест неуспешной регистрации пользователя без поля")
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
    public void testCreateUserWithOutField(String condition) {

        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);
        userData.remove(condition);
        Response responseCreateAuth = apiCoreRequests
                .makedPostRequest(url, userData);

        switch (condition) {
            case "username":
                Assertions.assertResponseTextEquals(
                        responseCreateAuth,
                        "The following required params are missed: username");
                break;
            case "firstName":
                Assertions.assertResponseTextEquals(
                        responseCreateAuth,
                        "The following required params are missed: firstName");
                break;
            case "lastName":
                Assertions.assertResponseTextEquals(
                        responseCreateAuth,
                        "The following required params are missed: lastName");
                break;
            case "email":
                Assertions.assertResponseTextEquals(
                        responseCreateAuth,
                        "The following required params are missed: email");
                break;
            case "password":
                Assertions.assertResponseTextEquals(
                        responseCreateAuth,
                        "The following required params are missed: password");
                break;
            default:
                throw new IllegalArgumentException("Condition is not known: " + condition);
        }
    }
}
