package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-reqest with token and auth cookie")
    public Response makedGetReauest(String url, String token, String cookie){
         return given()
                 .filter(new AllureRestAssured())
                 .header(new Header("x-csrf-token", token))
                 .cookie("auth_sid", cookie)
                 .get(url)
                 .andReturn();
     }

    @Step("Make a GET-reqest with auth cookie only")
    public Response makedGetWithCookie(String url, String cookie){
        return given()
                .filter(new AllureRestAssured())
                 .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-reqest with token only")
    public Response makedGetWithToken(String url, String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-reqest")
    public Response makedPostRequest(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

}
