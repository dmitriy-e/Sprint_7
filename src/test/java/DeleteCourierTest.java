import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.example.Courier;
import org.example.Credential;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class DeleteCourierTest {
    private static Credential credential;
    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        credential = new Credential("courier12345678", "12345678");
        courier = new Courier(credential.getLogin(), credential.getPassword(), "Alexey");
    }

    public void createNewCourier() {
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Test
    @DisplayName("Check deleting courier")
    public void deleteCourierSuccessfully() {
        createNewCourier();

        Integer courierId = given().header("Content-type", "application/json").and().body(credential).when().post("/api/v1/courier/login")
                .then().extract().body().path("id");

        given()
                .header("Content-type", "application/json")
                .delete(String.format("/api/v1/courier/%d", courierId))
                .then()
                .assertThat().body("ok", is(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Check deleting courier without id error")
    public void deleteCourierWithoutIdError() {
        given()
                .header("Content-type", "application/json")
                .and().body("{\"id\":}").when()
                .delete("/api/v1/courier/:id")
                .then()
                .assertThat().body("message", notNullValue())
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check deleting courier with invalid id error")
    public void deleteCourierWithInvalidId() {
        given()
                .header("Content-type", "application/json")
                .delete(String.format("/api/v1/courier/%d", -1))
                .then()
                .assertThat().body("message", containsString("Курьера с таким id нет"))
                .and()
                .statusCode(404);
    }
}

