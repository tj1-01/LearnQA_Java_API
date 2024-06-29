package tests;

import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.Assertions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

import lib.ApiCoreRequests;

@Epic("Autorisation cases")
@Feature("Autorisation")
public class UserAuthTestEx14 extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makedPostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth =this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    @Description("Тест успешно авторизирует пользователя по email и паролю")
    @DisplayName("Позитивный кейс авторизации")
    public void testAuthUser(){
        Response responceCheckAuth = apiCoreRequests
                .makedGetRequest(
                        "https://playground.learnqa.ru/api/user/auth",
                        this.header,
                        this.cookie );

        Assertions.assertJsonByName(responceCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Description("Тест проверки авторизации без куки или токена")
    @DisplayName("Тест неуспешной авторизации пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){
        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests
                    .makedGetWithCookie("https://playground.learnqa.ru/api/user/auth",
                            this.cookie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests
                    .makedGetWithToken("https://playground.learnqa.ru/api/user/auth",
                            this.header);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("condition is not known: " + condition);
        }
    }
}
