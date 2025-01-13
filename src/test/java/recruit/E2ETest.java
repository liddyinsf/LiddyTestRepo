package recruit;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class E2ETest {

    private static String candidateId;
    private static String token;
    private static String applicationId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://recruit-stage.portnov.com/recruit/api/v1";
    }

    @Test
    public void testE2E() {
        // Generate a 4-digit random value
        Random random = new Random();
        int randomValue = 1000 + random.nextInt(9000);
        String email = "candidate" + randomValue + "@portnov.com";

        // Create a new candidate
        Map<String, Object> candidate = new HashMap<>();
        candidate.put("firstName", "TestName");
        candidate.put("middleName", "");
        candidate.put("lastName", "TestLastname");
        candidate.put("email", email);
        candidate.put("password", "12345Abc");
        candidate.put("address", "");
        candidate.put("city", "");
        candidate.put("state", "CA");
        candidate.put("zip", "10000");
        candidate.put("summary", "");

        Response createCandidateResponse = given()
                .contentType(ContentType.JSON)
                .body(candidate)
                .when()
                .post("/candidates")
                .then()
                .statusCode(201)
                .extract().response();

        candidateId = createCandidateResponse.path("id").toString();

        // Login as the created candidate and get the token
        Map<String, String> loginCredentials = new HashMap<>();
        loginCredentials.put("email", "testmail1@fakemail.com");
        loginCredentials.put("password", "12345Abc");

        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginCredentials)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().response();

        token = loginResponse.path("token");

        // Create a new application
        Map<String, Object> application = new HashMap<>();
        application.put("candidateId", candidateId);
        application.put("positionId", 1);
        application.put("dateApplied", LocalDate.now().toString());

        Response createApplicationResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(application)
                .when()
                .post("/applications")
                .then()
                .statusCode(201)
                .extract().response();

        applicationId = createApplicationResponse.path("id").toString();

        // Validate the application
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/applications/" + applicationId)
                .then()
                .statusCode(200);

        // Update the candidate's name
        candidate.put("firstName", "Updated");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(candidate)
                .when()
                .put("/candidates/" + candidateId)
                .then()
                .statusCode(200);

        // Delete the application
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/applications/" + applicationId)
                .then()
                .statusCode(204);

        // Delete the candidate
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/candidates/" + candidateId)
                .then()
                .statusCode(204);
    }
}
