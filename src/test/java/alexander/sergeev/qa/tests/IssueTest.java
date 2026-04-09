package alexander.sergeev.qa.tests;

import alexander.sergeev.qa.model.EntityRef;
import alexander.sergeev.qa.model.request.CreateIssueRequest;
import alexander.sergeev.qa.model.request.CreateProjectRequest;
import alexander.sergeev.qa.model.request.UpdateIssueRequest;
import alexander.sergeev.qa.model.response.IssueRecord;
import alexander.sergeev.qa.model.response.ProjectRecord;
import alexander.sergeev.qa.util.CsvDataProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Test(dependsOnGroups = "auth")
public class IssueTest extends BaseTest {

    private String projectId;
    private String issueId;

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
                        "IT" + UUID.randomUUID().toString().substring(0, 3).toUpperCase(),
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
            given(spec).when().delete(PROJECTS_PATH + "/" + projectId).then().statusCode(200);
        }
    }

    @BeforeMethod(onlyForGroups = "issue-tests")
    public void createTestIssue() {
        IssueRecord issue = given(spec)
                .body(new CreateIssueRequest("test_" + UUID.randomUUID(), "Test description", new EntityRef(projectId)))
                .when()
                .post(ISSUES_PATH + ISSUE_FIELDS_QUERY)
                .then()
                .statusCode(200)
                .extract().as(IssueRecord.class);

        issueId = issue.id();
    }

    @AfterMethod(onlyForGroups = "issue-tests", alwaysRun = true)
    public void deleteTestIssue() {
        if (issueId != null) {
            given(spec).when().delete(ISSUES_PATH + "/" + issueId).then().statusCode(200);
            issueId = null;
        }
    }

    @DataProvider(name = "invalidIssues")
    public Object[][] invalidIssues() {
        return CsvDataProvider.load("data/issues_negative.csv");
    }

    @Test(description = "Создание задачи с валидными данными (200)",
            priority = 3)
    public void createIssue() {
        String summary = "test_" + UUID.randomUUID();

        IssueRecord issue = given(spec)
                .body(new CreateIssueRequest(summary, "Test description", new EntityRef(projectId)))
                .when()
                .post(ISSUES_PATH + ISSUE_FIELDS_QUERY)
                .then()
                .statusCode(200)
                .extract().as(IssueRecord.class);

        assertThat(issue.id(), notNullValue());
        assertThat(issue.idReadable(), notNullValue());
        assertThat(issue.summary(), equalTo(summary));
        assertThat(issue.project().id(), equalTo(projectId));
        assertThat(issue.type(), equalTo("Issue"));

        given(spec).when().delete(ISSUES_PATH + "/" + issue.id()).then().statusCode(200);
    }

    @Test(description = "Получение задачи по ID (200)",
            priority = 4,
            groups = "issue-tests")
    public void getIssueById() {
        IssueRecord issue = given(spec)
                .when()
                .get(ISSUES_PATH + "/" + issueId + ISSUE_FIELDS_QUERY)
                .then()
                .statusCode(200)
                .extract().as(IssueRecord.class);

        assertThat(issue.id(), equalTo(issueId));
        assertThat(issue.summary(), notNullValue());
        assertThat(issue.type(), equalTo("Issue"));
    }

    @Test(description = "Обновление summary задачи (200)",
            priority = 5,
            groups = "issue-tests")
    public void updateIssue() {
        String newSummary = "test_updated_" + UUID.randomUUID();

        IssueRecord updated = given(spec)
                .body(new UpdateIssueRequest(newSummary))
                .when()
                .post(ISSUES_PATH + "/" + issueId + ISSUE_FIELDS_QUERY)
                .then()
                .statusCode(200)
                .extract().as(IssueRecord.class);

        assertThat(updated.id(), equalTo(issueId));
        assertThat(updated.summary(), equalTo(newSummary));
    }

    @Test(description = "Создание задачи без обязательных полей (400)",
            dataProvider = "invalidIssues")
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

    @Test(description = "Запрос несуществующей задачи (404)")
    public void getIssueWithNonExistentId() {
        given(spec)
                .when()
                .get(ISSUES_PATH + "/3-99999?fields=id")
                .then()
                .statusCode(404)
                .body("error", equalTo("Not Found"));
    }
}
