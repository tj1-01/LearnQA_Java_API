package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@Epic("Get User Info Cases")
@Feature("Reading")
@Story("story2")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Проверка недоступности лишних данных без авторизации")
    @DisplayName("Неавторизованный запрос")
    @AllureId("2.1")
    @Tag("Security")
    public void testGetUserDataNotAuth() {
        Response responseUserData = apiCoreRequests
                .makeGetRequest(apiBaseUrl+"user/2");

        Assertions.assertJsonHasField(responseUserData, "username");

        String[] unExpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, unExpectedFields);
    }

    @Test
    @Description("Проверка доступности всех данных с авторизацией")
    @DisplayName("Авторизированный запрос")
    @Severity(CRITICAL)
    @AllureId("2.2")
    public void testGetUserDetailsAuthAsSameUser() {
        //LOGIN USER NUMBER 2
        Map<String, String> authData = new HashMap<>();

        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(apiBaseUrl+"user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //GET DATA
        Response responseUserData = apiCoreRequests
                .makeGetRequest(apiBaseUrl+"user/2", header, cookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @Description("Проверка недступности чужих данных")
    @DisplayName("Авторизированный запрос за чужими данные")
    @Tag("Security")
    @AllureId("2.3")
    public void testGetUserDetailsAuthAsAnotherUser() {
        //GENERATE USER AND LOGIN
        Response responseGetAuth = createAndGetAuth();
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //GET DATA USER 2
        Response responseUserData = apiCoreRequests
                .makeGetRequest(apiBaseUrl+"user/2", header, cookie);

        Assertions.assertJsonHasField(responseUserData, "username");

        String[] unExpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, unExpectedFields);
    }
}
