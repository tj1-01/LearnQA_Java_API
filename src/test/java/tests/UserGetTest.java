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

import java.util.HashMap;
import java.util.Map;

@Epic("Get User info Cases")
@Feature("UserInfo")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Проверка недоступности лишних данных без авторизации")
    @DisplayName("Неавторизованный запрос на данные")
    public void testGetUserDataNotAuth() {
        Response responseUserData = apiCoreRequests
                .makedGetRequest("https://playground.learnqa.ru/api/user/2");

        Assertions.assertJsonHasField(responseUserData, "username");

        String[] unExpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, unExpectedFields);
    }

    @Test
    @Description("Проверка доступности всех данных с авторизацией")
    @DisplayName("Авторизированный запрос")
    public void testGetUserDetailsAuthAsSameUser() {
        //LOGIN USER NUMBER 2
        Map<String, String> authData = new HashMap<>();

        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makedPostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //GET DATA
        Response responseUserData = apiCoreRequests
                .makedGetRequest("https://playground.learnqa.ru/api/user/2", header, cookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @Description("Проверка недступности чужих данных")
    @DisplayName("Авторизированный запрос за чужими данные")
    public void testGetUserDetailsAuthAsAnotherUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makedPostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData);

        //LOGIN USER
        Response responseGetAuth = apiCoreRequests
                .makedPostRequest("https://playground.learnqa.ru/api/user/login", userData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //GET DATA USER 2
        Response responseUserData = apiCoreRequests
                .makedGetRequest("https://playground.learnqa.ru/api/user/2", header, cookie);

        Assertions.assertJsonHasField(responseUserData, "username");

        String[] unExpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, unExpectedFields);
    }
}
