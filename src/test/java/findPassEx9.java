import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.*;

public class findPassEx9 {

    @Test
    public void findPass(){

        String login="super_admin";
        String checkPhrase="You are NOT authorized";

        String urlGetCookie = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String urlCheckAuth = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

        String test_pass="super_admin";

        List<String> testPass = new ArrayList<>();
        testPass = fullTestPassList();

        if (testPass.isEmpty()) {System.out.println("Списка паролей нет, тест прекращен"); return;}

        for (int i = 0; i < testPass.size(); i++) {
            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", testPass.get(i));

            Response response = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post(urlGetCookie)
                    .andReturn();

            String responseCookie = response.getCookie("auth_cookie");
            if (responseCookie == null) {
                System.out.println("Нет такого Логина, пробуйте другой.");
                return;
            }

            Response responseA = RestAssured
                    .given()
                    .body(data)
                    .cookie("auth_cookie",responseCookie)
                    .when()
                    .post(urlCheckAuth)
                    .andReturn();

            if ( !responseA.asString().equals(checkPhrase)) {
                System.out.println("Корректное сочетание: " + data);
                responseA.print();
                return;
            }
        }

    }

    public List<String> fullTestPassList(){

        List<String> testPasws = new ArrayList<>();

        String tag1="<td align=\"left\">";
        String tag2 ="</td>";

        int s1, s2;

        Response responseA = RestAssured
                .post("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords")
                .andReturn();

        String text = responseA.asString();
        int count = StringUtils.countMatches(text, tag1);
        for (int i = 0; i < count; i++) {
            s1 = text.indexOf(tag1);
            if (s1 > 0) {
                s2 = text.indexOf(tag2, s1);
                String Pass = text.substring(s1 + tag1.length(), s2-1);
                testPasws.add(Pass);
                text = text.substring(s2);
            }
        }

        Set<String> setUnicPswrd = new LinkedHashSet<>(testPasws);
        testPasws = new ArrayList<>(setUnicPswrd);

        return testPasws;
    }
}
