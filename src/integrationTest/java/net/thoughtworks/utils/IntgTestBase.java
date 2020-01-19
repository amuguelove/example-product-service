package net.thoughtworks.utils;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.apache.catalina.security.SecurityConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@Import({IntgTestConfig.class, SecurityConfig.class, MockConfig.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
        MockitoTestExecutionListener.class
})
@DbUnitConfiguration(
        databaseConnection = "dbUnitDatabaseConnection",
        dataSetLoader = YamlDataSetLoader.class)
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntgTestBase {

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        initAssured(port);
        auth();
    }

    private void initAssured(int port) {
        RestAssured.basePath = "/";
        RestAssured.port = port;

        RestAssured.requestSpecification =
                new RequestSpecBuilder().setContentType("application/json").build();
    }

    private void auth() {
        RestAssured.requestSpecification.auth().basic("admin", "password");
    }
}
