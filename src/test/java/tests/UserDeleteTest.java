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

import java.util.Map;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@Epic("Delete Info Cases")
@Feature("Deleting")
@Story("story4")
public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Проверка удаления пользователя c id=2 ")
    @DisplayName("Запрет удаления данных(id=2)")
    @Owner("Не vinkotov@example.com")
    @Tag("Security")
    @AllureId("4.1")
    public void testDeleteSecondUser() {
        //GENERATE USER AND LOGIN
        Response responseGetAuth = createAndGetAuth();
        int userId = Integer.parseInt(this.getStringFromJson(responseGetAuth, "user_id"));

        //TRY DELETE USER (ID=2)
        Response responseDel = apiCoreRequests
                .makeDeletedRequest(apiBaseUrl+"user/2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );
        Assertions.assertResponseCodeEquals(responseDel, 400);
        Assertions.assertResponseTextEquals(responseDel,
                "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }

    @Test
    @Description("Проверка удаления своих данных пользователем")
    @DisplayName("Позитивный кейс удаления")
    @Severity(CRITICAL)
    @AllureId("4.2")
    public void testDeleteUser() {
        //GENERATE USER AND LOGIN
        Response responseGetAuth = createAndGetAuth();
        int userId = Integer.parseInt(this.getStringFromJson(responseGetAuth, "user_id"));

        Response responseDel = apiCoreRequests
                .makeDeletedRequest(apiBaseUrl+"user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertResponseCodeEquals(responseDel, 200);

        Response responseUserData = apiCoreRequests
                .makeGetRequest(apiBaseUrl+"user/" + userId);
        Assertions.assertJsonHasNotField(responseUserData, "username");
    }

    @Test
    @Description("Проверка удаления пользователем чужих данных ")
    @DisplayName("Запрет удаления чужих данных")
    @Tag("Security")
    @AllureId("4.3")
    @TmsLink("TMS-001")
    public void testDeleteOtherUser() {
        //GENERATE USER AND LOGIN
        Response responseGetAuth = createAndGetAuth();
        int userId = Integer.parseInt(this.getStringFromJson(responseGetAuth, "user_id"));

        //GENERATE USER2
        Map<String, String> userNotEditData = DataGenerator.getRegistrationData();
        Response responseNotEditCreateAuth = apiCoreRequests
                .makePostRequest(apiBaseUrl+"user/", userNotEditData);
        int userNotEditId = Integer.parseInt(this.getStringFromJson(responseNotEditCreateAuth, "id"));

        //TRY DELETE USER2
        Response responseDel = apiCoreRequests
                .makeDeletedRequest(apiBaseUrl+"user/" + userNotEditId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertResponseCodeEquals(responseDel, 400);

        Response responseUserData = apiCoreRequests
                .makeGetRequest(apiBaseUrl+"user/" + userNotEditId);
        Assertions.assertJsonHasField(responseUserData, "username");
    }
}
