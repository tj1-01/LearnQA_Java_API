package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@Epic("Register User Cases")
@Feature("Creating")
@Story("story1")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String url = apiBaseUrl+"user/";

    @Test
    @Description("Успешная регестрирация нового пользователя")
    @DisplayName("Позитивный кейс авторизации")
    @Severity(CRITICAL)
    @AllureId("1.1")
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("Проверка уникальности email при регистрации")
    @DisplayName("Ошибка регистрации при повторном e-mail")
    @Tag("Uncorrected_data")
    @AllureId("1.2")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(
                responseCreateAuth,
                "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("Проверка запрета регистрации на некорректный email")
    @DisplayName("Ошибка регистрации с невалидным e-mail")
    @Tag("Uncorrected_data")
    @AllureId("1.3")
    public void testCreateUserWithBrokenEmail() {
        String badEmail = DataGenerator.getBadEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", badEmail);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(
                responseCreateAuth,
                "Invalid email format");
    }

    @Test
    @Description("Тест проверяет невозможность зарегестрировать пользователя с коротким именем")
    @DisplayName("Ошибка регистрации c длиной имени в 1 символ")
    @Tag("Uncorrected_data")
    @AllureId("1.4")
    public void testCreateUserShortName() {
        String name = DataGenerator.getNameByLenght(1);

        Map<String, String> userData = new HashMap<>();
        userData.put("username", name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(
                responseCreateAuth,
                "The value of 'username' field is too short");
    }

    @Test
    @Description("Тест проверяет невозможность зарегестрировать пользователя с длинным именем")
    @DisplayName("Ошибка регистрации c длиной имени длиннее 250 символов")
    @Tag("Uncorrected_data")
    @AllureId("1.5")
    public void testCreateUserTooLongName() {
        String name = DataGenerator.getNameByLenght(251);

        Map<String, String> userData = new HashMap<>();
        userData.put("username", name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(
                responseCreateAuth,
                "The value of 'username' field is too long");
    }

    @Description("Тест проверяет невозможность зарегестрировать пользователя без какого-либо поля")
    @DisplayName("Тест неуспешной регистрации пользователя без поля")
    @Tag("Uncorrected_data")
    @AllureId("1.6")
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
    public void testCreateUserWithOutField(String condition) {

        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);
        userData.remove(condition);
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(url, userData);

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
