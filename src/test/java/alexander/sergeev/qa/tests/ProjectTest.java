package alexander.sergeev.qa.tests;

import alexander.sergeev.qa.model.EntityRef;
import alexander.sergeev.qa.model.request.CreateProjectRequest;
import alexander.sergeev.qa.model.response.ProjectRecord;
import alexander.sergeev.qa.util.CsvDataProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Test(dependsOnGroups = "auth")
public class ProjectTest extends BaseTest {

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
                        "TP" + UUID.randomUUID().toString().substring(0, 3).toUpperCase(),
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

    @DataProvider(name = "invalidProjects")
    public Object[][] invalidProjects() {
        return CsvDataProvider.load("data/projects_negative.csv");
    }

    @Test(description = "Создание проекта с валидными данными (200)",
            priority = 1)
    public void createProject() {
        String leaderId = given(spec)
                .when()
                .get(USERS_ME_PATH + "?fields=id")
                .then()
                .statusCode(200)
                .extract().path("id");

        String name = "test_" + UUID.randomUUID();

        ProjectRecord project = given(spec)
                .body(new CreateProjectRequest(
                        name,
                        "CP" + UUID.randomUUID().toString().substring(0, 3).toUpperCase(),
                        new EntityRef(leaderId)))
                .when()
                .post(PROJECTS_PATH + PROJECT_FIELDS_QUERY)
                .then()
                .statusCode(200)
                .extract().as(ProjectRecord.class);

        assertThat(project.id(), notNullValue());
        assertThat(project.name(), equalTo(name));
        assertThat(project.type(), equalTo("Project"));

        given(spec)
                .when()
                .delete(PROJECTS_PATH + "/" + project.id())
                .then()
                .statusCode(200);
    }

    @Test(description = "Получение проекта по ID (200)",
            priority = 2)
    public void getProjectById() {
        ProjectRecord project = given(spec)
                .when()
                .get(PROJECTS_PATH + "/" + projectId + PROJECT_FIELDS_QUERY)
                .then()
                .statusCode(200)
                .extract().as(ProjectRecord.class);

        assertThat(project.id(), equalTo(projectId));
        assertThat(project.name(), notNullValue());
        assertThat(project.type(), equalTo("Project"));
    }

    @Test(description = "Создание проекта без обязательных полей (400)",
            dataProvider = "invalidProjects")
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
}
