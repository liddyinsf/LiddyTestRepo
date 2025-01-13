package recruit;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class RecruitTests {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://recruit-stage.portnov.com/recruit/api/v1";
    }

    // Get All Candidates
    @Test
    public void testGetAllCandidates() {
        given()
                .log().all()
                .when()
                .get("/candidates")
                .then()
                .log().all()
                .statusCode(200);
    }

    // Get All Positions
    @Test
    public void testGetAllPositions(){
        given()
                .log().all()
                .when()
                .get("/positions")
                .then()
                .log().all()
                .statusCode(200);
    }
}
