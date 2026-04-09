package alexander.sergeev.qa.tests;

import alexander.sergeev.qa.model.EntityRef;
import alexander.sergeev.qa.model.request.CreateCommentRequest;
import alexander.sergeev.qa.model.request.CreateIssueRequest;
import alexander.sergeev.qa.model.request.CreateProjectRequest;
import alexander.sergeev.qa.model.response.CommentRecord;
import alexander.sergeev.qa.model.response.IssueRecord;
import alexander.sergeev.qa.model.response.ProjectRecord;
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

@Test(dependsOnGroups = "auth")
public class CommentTest extends BaseTest {

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
                        "CT" + UUID.randomUUID().toString().substring(0, 3).toUpperCase(),
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

    @Test(description = "Добавление комментария к задаче (200)",
            priority = 6,
            groups = "issue-tests")
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
