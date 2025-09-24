import base.AuthHelper;
import base.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest extends BaseIntegrationTest {

    @Test
    public void shouldReturnPatientsWithValidToken() {
        String login = "testuser@test.com";
        String password = "password123";
        String token = AuthHelper.getJwtAuthenticationToken(login, password);
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/patients")
            .then()
            .statusCode(200)
            .body("patients", notNullValue());
    }
}
