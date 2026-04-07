package alexander.sergeev.qa.tests.api;

import alexander.sergeev.qa.config.ConfigManager;
import alexander.sergeev.qa.util.CsvDataProvider;
import alexander.sergeev.qa.model.EntityRef;
import alexander.sergeev.qa.model.request.CreateIssueRequest;
import alexander.sergeev.qa.model.request.CreateProjectRequest;
import alexander.sergeev.qa.model.response.ProjectRecord;
import alexander.sergeev.qa.tests.base.BaseTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.oauth2;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Test(groups = "negative")
public class NegativeApiTest extends BaseTest {

    private String projectId;

    @BeforeClass
    public void createTestProject() {
        String leaderId = given(spec)
                .when()
                .get(USERS_ME_PATH + "?fields=id")
                .then()
                .statusCode(200)
                .extract().path("id");

        ProjectRecord project = given(spec)
                .body(new CreateProjectRequest(
                        "test_" + UUID.randomUUID(),
                        "NP" + UUID.randomUUID().toString().substring(0, 3).toUpperCase(),
                        new EntityRef(leaderId)))
                .when()
                .post(PROJECTS_PATH + PROJECT_FIELDS_QUERY)
                .then()
                .statusCode(200)
                .extract().as(ProjectRecord.class);

        projectId = project.id();
    }

    @AfterClass(alwaysRun = true)
    public void deleteTestProject() {
        if (projectId != null) {
            given(spec)
                    .when()
                    .delete(PROJECTS_PATH + "/" + projectId)
                    .then()
                    .statusCode(200);
        }
    }

    @DataProvider(name = "negativeIssues")
    public Object[][] negativeIssues() {
        return CsvDataProvider.load("data/issues_negative.csv");
    }

    @DataProvider(name = "negativeProjects")
    public Object[][] negativeProjects() {
        return CsvDataProvider.load("data/projects_negative.csv");
    }

    @Test(description = "Создание задачи без обязательных полей (400)",
            dataProvider = "negativeIssues")
    public void createIssueWithInvalidData(String testCaseId, String summary, String useProject, String expectedStatus, String expectedError) {
        EntityRef project = Boolean.parseBoolean(useProject) ? new EntityRef(projectId) : null;
        String issueSummary = summary.isBlank() ? null : summary;

        given(spec)
                .body(new CreateIssueRequest(issueSummary, null, project))
                .when()
                .post(ISSUES_PATH + "?fields=id")
                .then()
                .statusCode(Integer.parseInt(expectedStatus))
                .body("error", containsString(expectedError));
    }

    @Test(description = "Создание проекта без обязательных полей (400)",
            dataProvider = "negativeProjects")
    public void createProjectWithInvalidData(String testCaseId, String shortName, String expectedStatus, String expectedError) {
        String leaderId = given(spec)
                .when()
                .get(USERS_ME_PATH + "?fields=id")
                .then()
                .statusCode(200)
                .extract().path("id");

        given(spec)
                .body(new CreateProjectRequest(null, shortName, new EntityRef(leaderId)))
                .when()
                .post(PROJECTS_PATH + "?fields=id")
                .then()
                .statusCode(Integer.parseInt(expectedStatus))
                .body("error", containsString(expectedError));
    }

    @Test(description = "Запрос несуществующей задачи (404)")
    public void getIssueWithNonExistentId() {
        given(spec)
                .when()
                .get(ISSUES_PATH + "/3-99999?fields=id")
                .then()
                .statusCode(404)
                .body("error", equalTo("Not Found"));
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