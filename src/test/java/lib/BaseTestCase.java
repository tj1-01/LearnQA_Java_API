package lib;

import io.qameta.allure.Step;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    public String apiBaseUrl = "https://playground.learnqa.ru/api/";
    //public String apiBaseUrl = "https://playground.learnqa.ru/api_dev/";

    protected String getHeader(Response Response, String name){
        Headers headers = Response.getHeaders();

        assertTrue(headers.hasHeaderWithName(name), "В Response нет заголовка " + name);
        return headers.getValue(name);
    }

    protected String getCookie(Response Response, String name){
        Map<String,String> cookies = Response.getCookies();

        assertTrue(cookies.containsKey(name), "В Response нет cookie " + name);
        return cookies.get(name);
    }

    protected int getIntFromJson(Response Response, String name){
        Response.then().assertThat().body("$", hasKey(name));
        return Response.jsonPath().getInt(name);
    }

    protected String getStringFromJson(Response Response, String name){
        Response.then().assertThat().body("$", hasKey(name));
        return Response.jsonPath().getString(name);
    }

    @Step("Create User and Auth")
    public Response createAndGetAuth(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(apiBaseUrl+"user/", userData);
        //LOGIN
        Response responseGetAuth = apiCoreRequests.makePostRequest(apiBaseUrl+"user/login", userData);
        return responseGetAuth;
    }
}
