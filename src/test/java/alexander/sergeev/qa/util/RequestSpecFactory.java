package alexander.sergeev.qa.util;

import alexander.sergeev.qa.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.oauth2;

public class RequestSpecFactory {

    public static RequestSpecification create(ConfigManager config) {
        return new RequestSpecBuilder()
                .setBaseUri(config.get("youtrack.base.url"))
                .setAuth(oauth2(config.getAuthToken()))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .build();
    }
}