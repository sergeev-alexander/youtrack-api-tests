package alexander.sergeev.qa.tests;

import alexander.sergeev.qa.config.ConfigManager;
import alexander.sergeev.qa.util.RequestSpecFactory;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    protected static String PROJECTS_PATH;
    protected static String ISSUES_PATH;
    protected static String USERS_ME_PATH;
    protected static String PROJECT_FIELDS_QUERY;
    protected static String ISSUE_FIELDS_QUERY;
    protected static String COMMENT_FIELDS_QUERY;

    protected static RequestSpecification spec;

    @BeforeSuite
    public void initSuite() {
        ConfigManager config = new ConfigManager("config.properties");

        PROJECTS_PATH = config.get("api.path.projects");
        ISSUES_PATH = config.get("api.path.issues");
        USERS_ME_PATH = config.get("api.path.users.me");
        PROJECT_FIELDS_QUERY = config.get("api.fields.project");
        ISSUE_FIELDS_QUERY = config.get("api.fields.issue");
        COMMENT_FIELDS_QUERY = config.get("api.fields.comment");

        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL));
        spec = RequestSpecFactory.create(config);
    }
}