package net.thoughtworks.product;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import net.thoughtworks.utils.IntgTestBase;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@DatabaseSetup(value = "classpath:/integration/product.yml")
@DatabaseTearDown
public class ProductApiTest extends IntgTestBase {

    @Test
    public void shouldGetAllProducts() {
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
        ;
    }
}
