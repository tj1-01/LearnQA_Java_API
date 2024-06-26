package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.Assertions;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;

import lib.ApiCoreRequests;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@Epic("Autorisation cases")
@Feature("Autorisation")
@Story("story5")
public class UserAuthTestEx14 extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    @Owner("vinkotov@example.com")
    public void loginUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(apiBaseUrl+"user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth =this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    @Description("Тест успешно авторизирует пользователя по email и паролю")
    @DisplayName("Позитивный кейс авторизации")
    @Severity(CRITICAL)
    @AllureId("5.1")
    public void testAuthUser(){
        Response responceCheckAuth = apiCoreRequests
                .makeGetRequest(
                        apiBaseUrl+"user/auth",
                        this.header,
                        this.cookie );

        Assertions.assertJsonByName(responceCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Description("Тест проверки авторизации без куки или токена")
    @DisplayName("Тест неуспешной авторизации пользователя")
    @Tag("Security")
    @AllureId("5.2")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){
        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests
                    .makeGetWithCookie(apiBaseUrl+"user/auth",
                            this.cookie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests
                    .makeGetWithToken(apiBaseUrl+"user/auth",
                            this.header);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("condition is not known: " + condition);
        }
    }
}
