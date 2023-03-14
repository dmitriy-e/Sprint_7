import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apitesting.Courier;
import org.apitesting.Credential;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class CreateCourierTest {
    private static Credential credential;
    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        credential = new Credential("courier12345678", "12345678");
        courier = new Courier(credential.getLogin(), credential.getPassword(), "Alexey");
    }

    public Response createNewCourier() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Test
    @DisplayName("Check creating new courier")
    public void createNewCourierAndCheckResponse() {
        createNewCourier().then().assertThat().body("ok", is(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Check new creation error without login or password")
    public void createNewCourierWithoutLoginOrPasswordError() {
        courier = new Courier("", "", "Alexey");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check new courier creation with duplicate")
    public void createNewCourierIfExistsConflict() {
        createNewCourier();
        createNewCourier().then().assertThat().body("message", containsString("Этот логин уже используется"))
                .and()
                .statusCode(409);
    }

    @AfterClass
    public static void deleteCourier() {
        Integer courierId = given().header("Content-type", "application/json").and().body(credential).when().post("/api/v1/courier/login")
                .then().extract().body().path("id");

        if (courierId != null) {
            given()
                    .header("Content-type", "application/json")
                    .delete(String.format("/api/v1/courier/%d", courierId));
        }
    }
}

