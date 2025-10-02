import base.AuthHelper;
import base.BaseIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void shouldReturn429AfterLimitExceeded() throws InterruptedException {
        // given
        String login = "testuser@test.com";
        String password = "password123";
        String token = AuthHelper.getJwtAuthenticationToken(login, password);
        int totalNumberOfRequestPerSecond = 10;
        int tooManyRequests = 0;
        // when
        for (int i = 1; i <= totalNumberOfRequestPerSecond; i++) {
            Response response = RestAssured.given()
                    .header("Authorization", "Bearer " + token)
                    .get("/api/patients");
            System.out.printf("Request %d -> Status: %d%n", i, response.statusCode());
            if (response.statusCode() == 429) {
                tooManyRequests++;
            }
            Thread.sleep(20);
        }
        // then
        assertTrue(tooManyRequests >= 1 && tooManyRequests <= totalNumberOfRequestPerSecond,
                "Expected at least 1 requests to be rate limited (429)");
    }
}
