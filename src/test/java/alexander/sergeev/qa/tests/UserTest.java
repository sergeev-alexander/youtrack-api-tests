package alexander.sergeev.qa.tests;

import alexander.sergeev.qa.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.oauth2;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Test(groups = "auth")
public class UserTest extends BaseTest {

    @Test(description = "Валидация токена аутентификации (200)")
    public void validateAuthToken() {
        given(spec)
                .when()
                .get(USERS_ME_PATH)
                .then()
                .statusCode(is(200))
                .contentType(containsString("application/json"))
                .body("$type", equalTo("Me"));
    }

    @Test(description = "Запрос с невалидным токеном (401)")
    public void requestWithInvalidToken() {
        ConfigManager config = new ConfigManager("config.properties");

        RequestSpecification invalidSpec = new RequestSpecBuilder()
                .setBaseUri(config.get("youtrack.base.url"))
                .setAuth(oauth2("invalid-token"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .build();

        given(invalidSpec)
                .when()
                .get(USERS_ME_PATH)
                .then()
                .statusCode(401)
                .body("error", equalTo("Unauthorized"));
    }
}
