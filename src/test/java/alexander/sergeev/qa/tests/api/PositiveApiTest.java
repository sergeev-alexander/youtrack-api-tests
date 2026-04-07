package alexander.sergeev.qa.tests.api;

import alexander.sergeev.qa.model.EntityRef;
import alexander.sergeev.qa.model.request.CreateCommentRequest;
import alexander.sergeev.qa.model.request.CreateIssueRequest;
import alexander.sergeev.qa.model.request.CreateProjectRequest;
import alexander.sergeev.qa.model.request.UpdateIssueRequest;
import alexander.sergeev.qa.model.response.CommentRecord;
import alexander.sergeev.qa.model.response.IssueRecord;
import alexander.sergeev.qa.model.response.ProjectRecord;
import alexander.sergeev.qa.tests.base.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Test(groups = "positive")
public class PositiveApiTest extends BaseTest {

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

    @Test(description = "Создание проекта с валидными данными (200)",
            priority = 1,
            groups = {"positive", "project-tests"})
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

        given(spec).when().delete(PROJECTS_PATH + "/" + project.id()).then().statusCode(200);
    }

    @Test(description = "Получение проекта по ID (200)",
            priority = 2,
            groups = {"positive", "project-tests"})
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

    @Test(description = "Создание задачи с валидными данными (200)",
            priority = 3,
            groups = {"positive", "issue-tests"})
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
            groups = {"positive", "issue-tests"})
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
            groups = {"positive", "issue-tests"})
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

    @Test(description = "Добавление комментария к задаче (200)",
            priority = 6,
            groups = {"positive", "issue-tests"})
    public void addComment() {
        String text = "test_comment_" + UUID.randomUUID();

        CommentRecord comment = given(spec)
                .body(new CreateCommentRequest(text))
                .when()
                .post(ISSUES_PATH + "/" + issueId + "/comments" + COMMENT_FIELDS_QUERY)
                .then()
                .statusCode(200)
                .extract().as(CommentRecord.class);

        assertThat(comment.id(), notNullValue());
        assertThat(comment.text(), equalTo(text));
        assertThat(comment.type(), equalTo("IssueComment"));
    }
}