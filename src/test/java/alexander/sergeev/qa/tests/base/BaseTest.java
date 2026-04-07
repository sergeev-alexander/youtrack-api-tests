package alexander.sergeev.qa.tests.base;

import alexander.sergeev.qa.config.ConfigManager;
import alexander.sergeev.qa.util.RequestSpecFactory;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    protected static final String PROJECTS_PATH = "/api/admin/projects";
    protected static final String ISSUES_PATH = "/api/issues";
    protected static final String USERS_ME_PATH = "/api/users/me";

    protected static final String PROJECT_FIELDS_QUERY = "?fields=id,name,shortName,leader(id,login),$type";
    protected static final String ISSUE_FIELDS_QUERY = "?fields=id,idReadable,summary,description,project(id,shortName),$type";
    protected static final String COMMENT_FIELDS_QUERY = "?fields=id,text,$type";

    protected static RequestSpecification spec;

    @BeforeSuite
    public void initSuite() {
        ConfigManager config = new ConfigManager("config.properties");
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL));
        spec = RequestSpecFactory.create(config);
    }
}