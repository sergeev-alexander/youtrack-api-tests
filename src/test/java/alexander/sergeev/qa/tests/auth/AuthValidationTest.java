package alexander.sergeev.qa.tests.auth;

import alexander.sergeev.qa.tests.base.BaseTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Test(groups = "auth")
public class AuthValidationTest extends BaseTest {

    @Test(description = "Валидация токена аутентификации (200)")
    public void validateAuthToken() {
        given(spec)
                .when()
                .get("/api/users/me")
                .then()
                .statusCode(is(200))
                .contentType(containsString("application/json"))
                .body("$type", equalTo("Me"));
    }
}
