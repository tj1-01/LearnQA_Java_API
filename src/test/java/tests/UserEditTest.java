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

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@Epic("Edit User Cases")
@Feature("Updating")
@Story("story3")
public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Проверка редактирования данных пользователя")
    @DisplayName("Редактирование своих данных")
    @Severity(CRITICAL)
    @AllureId("3.1")
    public void testEditJustCreatedTest() {
        //GENERATE USER AND LOGIN
        Response responseGetAuth = createAndGetAuth();
        int userId = Integer.parseInt(this.getStringFromJson(responseGetAuth, "user_id"));

        //EDIT
        String newName = "ChangedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(apiBaseUrl+"user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData
                );

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest(apiBaseUrl+"user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("Проверка редактирования данных другого пользователя")
    @DisplayName("Запрет редактирования чужих данных")
    @Tag("Security")
    @AllureId("3.2")
    @Flaky
    public void testEditJustCreatedTest2() {
        //GENERATE USER AND LOGIN
        Response responseGetAuth = createAndGetAuth();
        int userId = Integer.parseInt(this.getStringFromJson(responseGetAuth, "user_id"));

        //GENERATE USER2
        Map<String, String> userNotEditData = DataGenerator.getRegistrationData();
        Response responseNotEditCreateAuth = apiCoreRequests
                .makePostRequest(apiBaseUrl+"user/", userNotEditData);
        int userNotEditId = Integer.parseInt(this.getStringFromJson(responseNotEditCreateAuth, "id"));

        String newName = "ChangedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        //TRY EDIT USER
        Response responseEditUser = apiCoreRequests
                .makePutRequest(apiBaseUrl+"user/" + userNotEditId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(
                responseEditUser,
                "{\"error\":\"This user can only edit their own data.\"}");
    }


    @Test
    @Description("Проверка недоступности изменения данных без авторизации")
    @DisplayName("Неавторизованный запрос на изменение данных")
    @Tag("Security")
    @AllureId("3.3")
    public void testEditWithOutAuth() {

        //GENERATE USER FOR TRY EDIT
        Map<String, String> userNotEditData = DataGenerator.getRegistrationData();
        Response responseNotEditCreateAuth = apiCoreRequests
                .makePostRequest(apiBaseUrl+"user/", userNotEditData);
        int userNotEditId = Integer.parseInt(this.getStringFromJson(responseNotEditCreateAuth, "id"));

        String newName = "ChangedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        //TRY EDIT USER
        Response responseEditWithOutAuth = apiCoreRequests
                .unmakePutRequest(
                        apiBaseUrl+"user/" + userNotEditId,
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditWithOutAuth, 400);
        Assertions.assertResponseTextEquals(
                responseEditWithOutAuth,
                "{\"error\":\"Auth token not supplied\"}");
    }

    @Test
    @Description("Проверка запрета невалидных данных на редактировании данных пользователя")
    @DisplayName("Изменение на некорретный e-mail")
    @Tag("Uncorrected_data")
    @AllureId("3.4")
    public void testEditBadEmailTest() {
        //GENERATE USER AND LOGIN
        Response responseGetAuth = createAndGetAuth();
        int userId = Integer.parseInt(this.getStringFromJson(responseGetAuth, "user_id"));

        //EDIT
        String badEmail = DataGenerator.getBadEmail();
        Map<String, String> editData = new HashMap<>();
        editData.put("email", badEmail);
        editData = DataGenerator.getRegistrationData(editData);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(apiBaseUrl+"user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(
                responseEditUser,
                "{\"error\":\"Invalid email format\"}");
    }

    @Test
    @Description("Проверка запрета невалидных данных на редактировании данных пользователя")
    @DisplayName("Изменение на некорретное имя (длина 1) ")
    @Tag("Uncorrected_data")
    @AllureId("3.5")
    public void testEditBadNameTest() {
        //GENERATE USER AND LOGIN
        Response responseGetAuth = createAndGetAuth();
        int userId = Integer.parseInt(this.getStringFromJson(responseGetAuth, "user_id"));

        //EDIT
        String badName = DataGenerator.getNameByLenght(1);
        Map<String, String> editData = new HashMap<>();
        editData.put("username", badName);
        editData = DataGenerator.getRegistrationData(editData);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(apiBaseUrl+"user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(
                responseEditUser,
                "{\"error\":\"The value for field `username` is too short\"}");
    }
}